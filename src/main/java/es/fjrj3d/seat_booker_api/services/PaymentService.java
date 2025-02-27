package es.fjrj3d.seat_booker_api.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import es.fjrj3d.seat_booker_api.dtos.PaymentDTO;
import es.fjrj3d.seat_booker_api.dtos.TransactionDTO;
import es.fjrj3d.seat_booker_api.models.*;
import es.fjrj3d.seat_booker_api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository iPaymentRepository;

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private ISeatRepository iSeatRepository;

    @Autowired
    private ITicketRepository iTicketRepository;

    @Autowired
    private IScreeningRepository iScreeningRepository;

    public User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return iUserRepository.findByEmail(email).orElse(null);
    }

    public Seat checkSeatAvailability(Long seatId) {
        Seat seat = iSeatRepository.findById(seatId).orElse(null);
        if (seat == null || seat.getReserved()) {
            return null;
        }
        return seat;
    }

    public boolean areAllSeatsReserved(Long screeningId) {
        List<Seat> seats = iSeatRepository.findByScreeningId(screeningId);
        return seats.stream().allMatch(Seat::getReserved);
    }

    public PaymentDTO addPaymentMethod(String customerId, String paymentMethodId, User user) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.attach(PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build());

            Payment payment = new Payment();
            payment.setPaymentToken(paymentMethodId);
            payment.setPaymentType(paymentMethod.getType());
            payment.setPaymentId(paymentMethod.getId());
            payment.setUser(user);
            iPaymentRepository.save(payment);

            return new PaymentDTO(paymentMethod.getId(), paymentMethod.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TransactionDTO processSeatPayment(User user, Seat seat, String paymentMethodId) throws StripeException {

        if (!seat.getScreening().isAvailability()){
            return null;
        } else {
            String priceString = seat.getPrice();
            BigDecimal priceBigDecimal = new BigDecimal(priceString.replaceAll("[^\\d.]", ""));
            BigDecimal seatPrice = priceBigDecimal.multiply(BigDecimal.valueOf(100));
            Long seatPriceLong = seatPrice.longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setCustomer(user.getStripeCustomerId())
                    .setPaymentMethod(paymentMethodId)
                    .setAmount(seatPriceLong)
                    .setCurrency("eur")
                    .setConfirm(true)
                    .setReturnUrl("https://tu-sitio.com/return-url")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                Ticket ticket = new Ticket();
                ticket.setUser(user);
                ticket.setPrice(seat.getPrice());
                ticket.setSeatName(seat.getSeatName());
                ticket.setSchedule(seat.getScreening().getSchedule());
                ticket.setMovieName(seat.getScreening().getRoom().getMovie().getTitle());
                ticket.setRoomName(seat.getScreening().getRoom().getRoomName());
                iTicketRepository.save(ticket);

                seat.setReserved(true);
                iSeatRepository.save(seat);

                if (areAllSeatsReserved(seat.getScreening().getId())){
                    Optional<Screening> screeningOptional = iScreeningRepository.findById(seat.getScreening().getId());
                    Screening screening = screeningOptional.get();
                    screening.setAvailability(false);
                    iScreeningRepository.save(screening);
                } else {
                    Optional<Screening> screeningOptional = iScreeningRepository.findById(seat.getScreening().getId());
                    Screening screening = screeningOptional.get();
                    screening.setAvailability(true);
                    iScreeningRepository.save(screening);
                }
            }

            return new TransactionDTO(paymentIntent.getId(), paymentIntent.getStatus(), seat.getPrice());
        }
    }
}
