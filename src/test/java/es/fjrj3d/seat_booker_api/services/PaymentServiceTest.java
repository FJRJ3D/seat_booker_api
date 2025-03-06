package es.fjrj3d.seat_booker_api.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import es.fjrj3d.seat_booker_api.dtos.PaymentDTO;
import es.fjrj3d.seat_booker_api.dtos.TransactionDTO;
import es.fjrj3d.seat_booker_api.models.*;
import es.fjrj3d.seat_booker_api.repositories.IPaymentRepository;
import es.fjrj3d.seat_booker_api.repositories.ISeatRepository;
import es.fjrj3d.seat_booker_api.repositories.IScreeningRepository;
import es.fjrj3d.seat_booker_api.repositories.ITicketRepository;
import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private IPaymentRepository iPaymentRepository;

    @Mock
    private IUserRepository iUserRepository;

    @Mock
    private ISeatRepository iSeatRepository;

    @Mock
    private ITicketRepository iTicketRepository;

    @Mock
    private IScreeningRepository iScreeningRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private Seat seat;
    private Screening screening;
    private Room room;
    private Movie movie;
    private final String paymentMethodId = "pm_test";
    private final String customerId = "cus_test";
    private final String priceString = "10.00";
    private BigDecimal seatPrice;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setStripeCustomerId(customerId);
        user.setEmail("test@example.com");

        movie = new Movie();
        movie.setTitle("Test Movie");

        room = new Room();
        room.setRoomName("Room 1");
        room.setMovie(movie);

        screening = new Screening();
        screening.setId(100L);
        screening.setAvailability(true);
        screening.setRoom(room);
        screening.setSchedule(LocalTime.of(20, 0));

        seat = new Seat();
        seat.setId(1L);
        seat.setPrice(priceString);
        seat.setReserved(false);
        seat.setSeatName("A1");
        seat.setScreening(screening);

        seatPrice = new BigDecimal(priceString.replaceAll("[^\\d.]", ""));
    }

    @Test
    public void test_get_user_from_authentication() {
        String email = "test@example.com";
        User returnedUser = new User();
        returnedUser.setEmail(email);
        when(iUserRepository.findByEmail(email)).thenReturn(Optional.of(returnedUser));
        Authentication auth = new UsernamePasswordAuthenticationToken(email, "password");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        User result = paymentService.getUserFromAuthentication();

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    public void test_check_seat_availability_when_seat_is_available() {
        when(iSeatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));
        Seat availableSeat = paymentService.checkSeatAvailability(seat.getId());
        assertNotNull(availableSeat);
        assertEquals(seat.getId(), availableSeat.getId());
    }

    @Test
    public void test_check_seat_availability_when_seat_is_reserved() {
        seat.setReserved(true);
        when(iSeatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));
        Seat availableSeat = paymentService.checkSeatAvailability(seat.getId());
        assertNull(availableSeat);
    }

    @Test
    public void test_are_all_seats_reserved_all_reserved() {
        seat.setReserved(true);
        Seat seat2 = new Seat();
        seat2.setReserved(true);
        seat2.setScreening(screening);
        List<Seat> seats = Arrays.asList(seat, seat2);
        when(iSeatRepository.findByScreeningId(screening.getId())).thenReturn(seats);
        boolean result = paymentService.areAllSeatsReserved(screening.getId());
        assertTrue(result);
    }

    @Test
    public void test_are_all_seats_reserved_not_all_reserved() {
        Seat seat2 = new Seat();
        seat2.setReserved(false);
        seat2.setScreening(screening);
        List<Seat> seats = Arrays.asList(seat, seat2);
        when(iSeatRepository.findByScreeningId(screening.getId())).thenReturn(seats);
        boolean result = paymentService.areAllSeatsReserved(screening.getId());
        assertFalse(result);
    }

    @Test
    public void test_add_payment_method_success() throws StripeException {
        try (MockedStatic<PaymentMethod> mockedStatic = mockStatic(PaymentMethod.class)) {
            PaymentMethod paymentMethodMock = mock(PaymentMethod.class);
            when(paymentMethodMock.getId()).thenReturn(paymentMethodId);
            when(paymentMethodMock.getType()).thenReturn("card");
            mockedStatic.when(() -> PaymentMethod.retrieve(paymentMethodId)).thenReturn(paymentMethodMock);

            doReturn(null).when(paymentMethodMock).attach(any(PaymentMethodAttachParams.class));

            PaymentDTO result = paymentService.addPaymentMethod(customerId, paymentMethodId, user);

            assertNotNull(result);
            assertEquals(paymentMethodId, result.paymentId());
            assertEquals("card", result.paymentType());
            verify(iPaymentRepository, times(1)).save(any(Payment.class));
        }
    }

    @Test
    public void test_process_seat_payment_successful_payment() throws StripeException {
        PaymentIntent paymentIntentMock = mock(PaymentIntent.class);
        when(paymentIntentMock.getStatus()).thenReturn("succeeded");
        when(paymentIntentMock.getId()).thenReturn("pi_123");

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);

            when(iTicketRepository.save(any(Ticket.class))).thenReturn(new Ticket());
            when(iSeatRepository.save(seat)).thenReturn(seat);
            when(iScreeningRepository.findById(screening.getId())).thenReturn(Optional.of(screening));

            TransactionDTO transactionDTO = paymentService.processSeatPayment(user, seat, paymentMethodId);
            assertNotNull(transactionDTO);
            assertEquals("pi_123", transactionDTO.id());
            assertEquals("succeeded", transactionDTO.status());
            assertEquals(priceString, transactionDTO.amount());
            verify(iTicketRepository, times(1)).save(any(Ticket.class));
            verify(iSeatRepository, times(1)).save(seat);
            verify(iScreeningRepository, atLeast(1)).save(any(Screening.class));
        }
    }

    @Test
    public void test_process_seat_payment_payment_fails() throws StripeException {
        PaymentIntent paymentIntentMock = mock(PaymentIntent.class);
        when(paymentIntentMock.getStatus()).thenReturn("failed");
        when(paymentIntentMock.getId()).thenReturn("pi_456");

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);

            TransactionDTO transactionDTO = paymentService.processSeatPayment(user, seat, paymentMethodId);
            assertNotNull(transactionDTO);
            assertEquals("failed", transactionDTO.status());
            verify(iTicketRepository, times(0)).save(any(Ticket.class));
            verify(iSeatRepository, times(0)).save(seat);
        }
    }

    @Test
    public void test_process_seat_payment_when_screening_not_available() throws StripeException {
        screening.setAvailability(false);
        TransactionDTO transactionDTO = paymentService.processSeatPayment(user, seat, paymentMethodId);
        assertNull(transactionDTO);
    }
}