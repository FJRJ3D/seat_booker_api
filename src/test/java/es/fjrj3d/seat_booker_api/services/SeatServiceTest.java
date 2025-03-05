package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.models.Seat;
import es.fjrj3d.seat_booker_api.repositories.ISeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SeatServiceTest {

    @Mock
    private ISeatRepository iSeatRepository;

    @InjectMocks
    private SeatService seatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_create_seats_for_screening_normal() {
        Screening screening = new Screening();
        Room room = new Room();
        room.setRowQuantity(3);
        room.setSeatQuantity(4);

        seatService.createSeatsForScreening(screening, room);

        List<Seat> seats = screening.getSeats();
        assertNotNull(seats);
        assertEquals(12, seats.size());

        assertEquals("Seat A-1", seats.get(0).getSeatName());
        assertEquals("Seat A-2", seats.get(1).getSeatName());
        assertEquals("Seat A-3", seats.get(2).getSeatName());
        assertEquals("Seat A-4", seats.get(3).getSeatName());

        assertEquals("Seat B-1", seats.get(4).getSeatName());
        assertEquals("Seat B-2", seats.get(5).getSeatName());
        assertEquals("Seat B-3", seats.get(6).getSeatName());
        assertEquals("Seat B-4", seats.get(7).getSeatName());

        assertEquals("Seat C-1", seats.get(8).getSeatName());
        assertEquals("Seat C-2", seats.get(9).getSeatName());
        assertEquals("Seat C-3", seats.get(10).getSeatName());
        assertEquals("Seat C-4", seats.get(11).getSeatName());

        for (Seat seat : seats) {
            assertEquals(screening, seat.getScreening());
        }

        verify(iSeatRepository, times(1)).saveAll(seats);
    }

    @Test
    public void test_create_seats_for_screening_row_overflow() {
        Screening screening = new Screening();
        Room room = new Room();
        room.setRowQuantity(27);
        room.setSeatQuantity(1);

        seatService.createSeatsForScreening(screening, room);

        List<Seat> seats = screening.getSeats();
        assertNotNull(seats);
        assertEquals(27, seats.size());
        assertEquals("Seat A-1", seats.get(0).getSeatName());
        assertEquals("Seat Z-1", seats.get(25).getSeatName());
        assertEquals("Seat ?-1", seats.get(26).getSeatName());
    }
}