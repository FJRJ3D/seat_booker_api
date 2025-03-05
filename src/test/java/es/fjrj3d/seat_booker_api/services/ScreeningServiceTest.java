package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.ScreeningNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.repositories.IScreeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ScreeningServiceTest {

    @Mock
    private IScreeningRepository iScreeningRepository;

    @Mock
    private IRoomRepository iRoomRepository;

    @Mock
    private SeatService seatService;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ScreeningService screeningService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_create_screening() {
        Room room = new Room();
        Movie movie = new Movie();
        movie.setDuration(LocalTime.of(2, 0));
        when(iScreeningRepository.save(any(Screening.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(seatService).createSeatsForScreening(any(Screening.class), eq(room));
        screeningService.createScreening(room, movie);
        verify(iScreeningRepository, atLeast(1)).save(any(Screening.class));
        verify(seatService, atLeast(1)).createSeatsForScreening(any(Screening.class), eq(room));
    }

    @Test
    public void test_get_all_screenings() {
        List<Screening> screenings = Arrays.asList(new Screening(), new Screening());
        when(iScreeningRepository.findAll()).thenReturn(screenings);
        List<Screening> result = screeningService.getAllScreenings();
        assertEquals(screenings, result);
    }

    @Test
    public void test_get_screening_by_id_found() {
        Screening screening = new Screening();
        screening.setId(1L);
        when(iScreeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        Optional<Screening> result = screeningService.getScreeningById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void test_get_screening_by_id_not_found() {
        when(iScreeningRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Screening> result = screeningService.getScreeningById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_get_all_screenings_by_room_found() {
        Room room = new Room();
        room.setId(10L);
        when(iRoomRepository.findByRoomName("Room 1")).thenReturn(Optional.of(room));
        List<Screening> screenings = Arrays.asList(new Screening(), new Screening());
        when(iScreeningRepository.findByRoomId(10L)).thenReturn(screenings);
        List<Screening> result = screeningService.getAllScreeningsByRoom("Room 1");
        assertEquals(screenings, result);
    }

    @Test
    public void test_get_all_screenings_by_room_not_found() {
        when(iRoomRepository.findByRoomName("Nonexistent")).thenReturn(Optional.empty());
        Exception exception = assertThrows(RoomNotFoundException.class, () -> screeningService.getAllScreeningsByRoom("Nonexistent"));
        assertEquals("Room not found with name: Nonexistent", exception.getMessage());
    }

    @Test
    public void test_update_screening() {
        Screening screening = new Screening();
        screening.setId(5L);
        when(iScreeningRepository.save(any(Screening.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Screening result = screeningService.updateScreening(screening, 5L);
        assertEquals(5L, result.getId());
    }

    @Test
    public void test_delete_screening_found() {
        when(iScreeningRepository.existsById(1L)).thenReturn(true);
        boolean result = screeningService.deleteScreening(1L);
        assertTrue(result);
        verify(iScreeningRepository).deleteById(1L);
    }

    @Test
    public void test_delete_screening_not_found() {
        when(iScreeningRepository.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(ScreeningNotFoundException.class, () -> screeningService.deleteScreening(1L));
        assertEquals("Screening not found with ID: 1", exception.getMessage());
    }

    @Test
    public void test_calculate_remaining_duration() {
        Screening screening = new Screening();
        screening.setSchedule(LocalTime.now().minusMinutes(10));
        screening.setDuration(Duration.ofMinutes(30));
        Duration remaining = screeningService.calculateRemainingDuration(screening);
        long diff = remaining.toMinutes();
        assertTrue(diff >= 19 && diff <= 21);
    }

    @Test
    public void test_calculate_time_until_screening() {
        Screening screening = new Screening();
        screening.setSchedule(LocalTime.now().plusMinutes(20));
        Duration timeUntil = screeningService.calculateTimeUntilScreening(screening);
        long diff = timeUntil.toMinutes();
        assertTrue(diff >= 19 && diff <= 21);
    }

    @Test
    public void test_send_duration_updates() {
        LocalDateTime now = LocalDateTime.now();
        Screening screeningA = new Screening();
        screeningA.setId(1L);
        screeningA.setSchedule(now.minusMinutes(10).toLocalTime());
        screeningA.setDuration(Duration.ofMinutes(30));
        Screening screeningB = new Screening();
        screeningB.setId(2L);
        screeningB.setSchedule(now.minusMinutes(40).toLocalTime());
        screeningB.setDuration(Duration.ofMinutes(30));
        Screening screeningC = new Screening();
        screeningC.setId(3L);
        screeningC.setSchedule(now.plusMinutes(20).toLocalTime());
        screeningC.setDuration(Duration.ofMinutes(30));
        List<Screening> screenings = Arrays.asList(screeningA, screeningB, screeningC);
        when(iScreeningRepository.findAll()).thenReturn(screenings);
        when(paymentService.areAllSeatsReserved(1L)).thenReturn(true);
        when(paymentService.areAllSeatsReserved(3L)).thenReturn(false);
        when(iScreeningRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return screenings.stream().filter(s -> s.getId().equals(id)).findFirst();
        });
        screeningService.sendDurationUpdates();
        verify(webSocketService, atLeastOnce()).sendDurationUpdate(eq(1L), anyString());
        verify(webSocketService, atLeastOnce()).sendScreeningEnded(eq(2L));
        verify(webSocketService, atLeastOnce()).sendDurationUpdate(eq(3L), anyString());
    }
}