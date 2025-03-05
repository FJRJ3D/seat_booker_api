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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {

    @Mock
    private IRoomRepository iRoomRepository;

    @Mock
    private IMovieRepository iMovieRepository;

    @Mock
    private ScreeningService screeningService;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_create_room_no_conflict() {
        Movie movie = new Movie();
        when(iRoomRepository.existsByRoomName("Room 1")).thenReturn(false);
        when(iRoomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(screeningService).createScreening(any(Room.class), eq(movie));
        Room createdRoom = roomService.createRoom(movie);
        assertEquals("Room 1", createdRoom.getRoomName());
        assertEquals(10, createdRoom.getRowQuantity());
        assertEquals(12, createdRoom.getSeatQuantity());
        assertEquals(movie, createdRoom.getMovie());
        verify(screeningService).createScreening(any(Room.class), eq(movie));
    }

    @Test
    public void test_create_room_with_conflict() {
        Movie movie = new Movie();
        when(iRoomRepository.existsByRoomName("Room 1")).thenReturn(true);
        when(iRoomRepository.existsByRoomName("Room 2")).thenReturn(false);
        when(iRoomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(screeningService).createScreening(any(Room.class), eq(movie));
        Room createdRoom = roomService.createRoom(movie);
        assertEquals("Room 2", createdRoom.getRoomName());
        assertEquals(10, createdRoom.getRowQuantity());
        assertEquals(12, createdRoom.getSeatQuantity());
        assertEquals(movie, createdRoom.getMovie());
        verify(screeningService).createScreening(any(Room.class), eq(movie));
    }

    @Test
    public void test_get_all_rooms() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room());
        roomList.add(new Room());
        when(iRoomRepository.findAll()).thenReturn(roomList);
        List<Room> result = roomService.getAllRooms();
        assertEquals(roomList, result);
    }

    @Test
    public void test_get_room_by_id_found() {
        Room room = new Room();
        room.setRoomName("Test Room");
        when(iRoomRepository.findById(1L)).thenReturn(Optional.of(room));
        Room result = roomService.getRoomById(1L);
        assertEquals("Test Room", result.getRoomName());
    }

    @Test
    public void test_get_room_by_id_not_found() {
        when(iRoomRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(1L));
        assertEquals("Room not found with id: 1", exception.getMessage());
    }

    @Test
    public void test_get_rooms_by_movie_title_found() {
        Movie movie = new Movie();
        movie.setId(1L);
        when(iMovieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(movie));
        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room());
        when(iRoomRepository.findByMovieId(1L)).thenReturn(roomList);
        List<Room> result = roomService.getRoomsByMovieTitle("Test Movie");
        assertEquals(roomList, result);
    }

    @Test
    public void test_get_rooms_by_movie_title_not_found() {
        when(iMovieRepository.findByTitle("Nonexistent Movie")).thenReturn(Optional.empty());
        Exception exception = assertThrows(MovieNotFoundException.class, () -> roomService.getRoomsByMovieTitle("Nonexistent Movie"));
        assertEquals("Movie not found with title: Nonexistent Movie", exception.getMessage());
    }

    @Test
    public void test_update_room_found() {
        Room existingRoom = new Room();
        existingRoom.setRoomName("Old Name");
        existingRoom.setRowQuantity(10);
        existingRoom.setSeatQuantity(12);
        when(iRoomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(iRoomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Room updateData = new Room();
        updateData.setRoomName("New Name");
        updateData.setRowQuantity(8);
        updateData.setSeatQuantity(9);
        Room updatedRoom = roomService.updateRoom(updateData, 1L);
        assertEquals("New Name", updatedRoom.getRoomName());
        assertEquals(8, updatedRoom.getRowQuantity());
        assertEquals(9, updatedRoom.getSeatQuantity());
    }

    @Test
    public void test_update_room_not_found() {
        when(iRoomRepository.findById(1L)).thenReturn(Optional.empty());
        Room updateData = new Room();
        Exception exception = assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(updateData, 1L));
        assertEquals("Room not found with ID: 1", exception.getMessage());
    }

    @Test
    public void test_delete_room_found() {
        when(iRoomRepository.existsById(1L)).thenReturn(true);
        String result = roomService.deleteRoom(1L);
        assertEquals("Room was successfully deleted", result);
        verify(iRoomRepository).deleteById(1L);
    }

    @Test
    public void test_delete_room_not_found() {
        when(iRoomRepository.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(1L));
        assertEquals("Room not found with id: 1", exception.getMessage());
    }

    @Test
    public void test_get_room_by_name_found() {
        Room room = new Room();
        room.setRoomName("Room 1");
        when(iRoomRepository.findByRoomName("Room 1")).thenReturn(Optional.of(room));
        Room result = roomService.getRoomByName("Room 1");
        assertEquals("Room 1", result.getRoomName());
    }

    @Test
    public void test_get_room_by_name_not_found() {
        when(iRoomRepository.findByRoomName("Room X")).thenReturn(Optional.empty());
        Exception exception = assertThrows(RoomNotFoundException.class, () -> roomService.getRoomByName("Room X"));
        assertEquals("Room not found with name: Room X", exception.getMessage());
    }

    @Test
    public void test_delete_rooms_by_ids_success() {
        List<Long> roomIds = Arrays.asList(1L, 2L, 3L);
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());
        when(iRoomRepository.findAllById(roomIds)).thenReturn(rooms);
        String result = roomService.deleteRoomsByIds(roomIds);
        assertEquals("Rooms were successfully deleted", result);
        verify(iRoomRepository).deleteAll(rooms);
    }

    @Test
    public void test_delete_rooms_by_ids_not_all_found() {
        List<Long> roomIds = Arrays.asList(1L, 2L, 3L);
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        rooms.add(new Room());
        when(iRoomRepository.findAllById(roomIds)).thenReturn(rooms);
        Exception exception = assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoomsByIds(roomIds));
        assertEquals("Some rooms not found", exception.getMessage());
    }

    @Test
    public void test_delete_rooms_by_ids_empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> roomService.deleteRoomsByIds(new ArrayList<>()));
        assertEquals("Room IDs cannot be null or empty", exception.getMessage());
    }
}