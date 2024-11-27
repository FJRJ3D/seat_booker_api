package es.fjrj3d.seat_booker_api.services;

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
    private IRoomRepository iRoomRepository;

    @Mock
    private IMovieRepository iMovieRepository;

    @InjectMocks
    private RoomService roomService;

    private Movie interstellar;
    private Room room1;
    private Room room2;

    private final List<Room> roomList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        interstellar = new Movie();
        interstellar.setId(1L);
        interstellar.setTitle("Interstellar");

        room1 = new Room();
        room1.setId(1L);
        room1.setMovie(interstellar);

        room2 = new Room();
        room2.setId(2L);
        room2.setMovie(interstellar);

        roomList.add(room1);
        roomList.add(room2);
    }

    @Test
    void should_create_room_with_unique_name() {
        when(iMovieRepository.findByTitle("Interstellar")).thenReturn(Optional.of(interstellar));
        when(iRoomRepository.existsByRoomName("Room 1")).thenReturn(true);
        when(iRoomRepository.existsByRoomName("Room 2")).thenReturn(false);
        when(iRoomRepository.save(any(Room.class))).thenReturn(room2);

        Room result = roomService.createRoom(room2, "Interstellar");

        assertNotNull(result);
        assertEquals("Room 2", result.getRoomName());
        assertEquals(interstellar, result.getMovie());
        verify(iRoomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void should_return_all_rooms() {
        when(iRoomRepository.findAll()).thenReturn(roomList);

        List<Room> result = roomService.getAllRooms();

        assertEquals(2, result.size());
        verify(iRoomRepository, times(1)).findAll();
    }

    @Test
    void should_return_room_by_id() {
        when(iMovieRepository.findByTitle("Interstellar")).thenReturn(Optional.of(interstellar));
        when(iRoomRepository.findById(1L)).thenReturn(Optional.of(room1));
        when(iRoomRepository.save(any(Room.class))).thenReturn(room1);

        roomService.createRoom(room1, "Interstellar");
        Room result = roomService.getRoomById(1L);

        assertNotNull(result);
        assertEquals("Room 1", result.getRoomName());
        verify(iRoomRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_when_room_not_found_by_id() {
        when(iRoomRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(3L));
        verify(iRoomRepository, times(1)).findById(3L);
    }

    @Test
    void should_return_rooms_by_movie_title() {
        when(iMovieRepository.findByTitle("Interstellar")).thenReturn(Optional.of(interstellar));
        when(iRoomRepository.findByMovieId(1L)).thenReturn(roomList);

        List<Room> result = roomService.getRoomsByMovieTitle("Interstellar");

        assertEquals(2, result.size());
        verify(iRoomRepository, times(1)).findByMovieId(1L);
    }

    @Test
    void should_update_room_when_room_exists() {
        when(iRoomRepository.findById(1L)).thenReturn(Optional.of(room1));
        room1.setRoomName("Updated Room");
        when(iRoomRepository.save(room1)).thenReturn(room1);

        Room result = roomService.updateRoom(room1, 1L);

        assertEquals("Updated Room", result.getRoomName());
        verify(iRoomRepository, times(1)).save(room1);
    }

    @Test
    void should_throw_exception_when_updating_non_existing_room() {
        when(iRoomRepository.findById(3L)).thenReturn(Optional.empty());

        Room newRoom = new Room();
        newRoom.setRoomName("New Room");

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(newRoom, 3L));
        verify(iRoomRepository, times(0)).save(any(Room.class));
    }

    @Test
    void should_delete_room_when_exists() {
        when(iRoomRepository.existsById(2L)).thenReturn(true);

        roomService.deleteRoom(2L);

        verify(iRoomRepository, times(1)).deleteById(2L);
    }

    @Test
    void should_throw_exception_when_room_not_found_for_deletion() {
        when(iRoomRepository.existsById(3L)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(3L));
        verify(iRoomRepository, times(1)).existsById(3L);
    }
}
