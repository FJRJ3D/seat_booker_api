package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private IRoomRepository roomRepository;

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room1;
    private Room room2;
    private Movie movie;

    private final List<Room> roomList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        room1 = new Room();
        room1.setId(1L);
        room1.setRoomName("Room 1");
        room1.setRowQuantity(10);
        room1.setSeatQuantity(100);
        room1.setMovie(movie);

        room2 = new Room();
        room2.setId(2L);
        room2.setRoomName("Room 2");
        room2.setRowQuantity(15);
        room2.setSeatQuantity(150);
        room2.setMovie(movie);

        roomList.add(room1);
        roomList.add(room2);
    }

    @Test
    void should_create_room_when_valid_data_is_provided() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(roomRepository.save(any(Room.class))).thenReturn(room1);

        Room result = roomService.createRoom(room1, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Room 1", result.getRoomName());
        assertEquals(10, result.getRowQuantity());
        assertEquals(100, result.getSeatQuantity());
        assertEquals(movie, result.getMovie());
        verify(roomRepository, times(1)).save(room1);
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_when_movie_not_found_for_create_room() {
        when(movieRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> roomService.createRoom(room1, 3L));
        verify(movieRepository, times(1)).findById(3L);
    }

    @Test
    void should_return_all_rooms() {
        when(roomRepository.findAll()).thenReturn(roomList);

        List<Room> result = roomService.getAllRooms();

        assertEquals(2, result.size());
        assertEquals("Room 1", result.get(0).getRoomName());
        assertEquals("Room 2", result.get(1).getRoomName());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void should_return_room_by_id() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));

        Room result = roomService.getRoomById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Room 1", result.getRoomName());
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_when_room_not_found_by_id() {
        when(roomRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(3L));
        verify(roomRepository, times(1)).findById(3L);
    }

    @Test
    void should_update_room_when_exists() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.save(room1)).thenReturn(room1);

        Room result = roomService.updateRoom(room1, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Room 1", result.getRoomName());
        assertEquals(10, result.getRowQuantity());
        assertEquals(100, result.getSeatQuantity());
        verify(roomRepository, times(1)).existsById(1L);
        verify(roomRepository, times(1)).save(room1);
    }

    @Test
    void should_throw_exception_when_room_not_found_for_update() {
        when(roomRepository.existsById(3L)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(room1, 3L));
        verify(roomRepository, times(1)).existsById(3L);
    }

    @Test
    void should_delete_room_when_exists() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.deleteRoom(1L);

        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    void should_throw_exception_when_room_not_found_for_deletion() {
        when(roomRepository.existsById(3L)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(3L));
        verify(roomRepository, times(1)).existsById(3L);
    }
}
