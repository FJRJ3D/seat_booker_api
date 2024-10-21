package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.services.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private MockMvc mockMvc;

    private Room room1;
    private Room room2;

    private final List<Room> roomList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();

        room1 = new Room();
        room1.setId(1L);
        room1.setRoomName("Room 1");
        room1.setRowQuantity(10);
        room1.setSeatQuantity(100);

        room2 = new Room();
        room2.setId(2L);
        room2.setRoomName("Room 2");
        room2.setRowQuantity(8);
        room2.setSeatQuantity(80);

        roomList.add(room1);
        roomList.add(room2);
    }

    @Test
    void should_create_room() throws Exception {
        when(roomService.createRoom(any(Room.class), anyLong())).thenReturn(room1);

        mockMvc.perform(post("/api/room/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomName\":\"Room 1\",\"rowQuantity\":10,\"seatQuantity\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.roomName").value("Room 1"));
    }

    @Test
    void should_return_all_rooms() throws Exception {
        when(roomService.getAllRooms()).thenReturn(roomList);

        mockMvc.perform(get("/api/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomName").value("Room 1"))
                .andExpect(jsonPath("$[1].roomName").value("Room 2"));
    }

    @Test
    void should_return_room_by_id() throws Exception {
        when(roomService.getRoomById(1L)).thenReturn(room1);

        mockMvc.perform(get("/api/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName").value("Room 1"));
    }

    @Test
    void should_return_not_found_for_nonexistent_room() throws Exception {
        when(roomService.getRoomById(3L)).thenReturn(null);

        mockMvc.perform(get("/api/room/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_room() throws Exception {
        when(roomService.updateRoom(any(Room.class), eq(2L))).thenReturn(room2);

        mockMvc.perform(put("/api/room/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomName\":\"Room 2\",\"rowQuantity\":8,\"seatQuantity\":80}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName").value("Room 2"));
    }

    @Test
    void should_return_not_found_for_update_when_room_does_not_exist() throws Exception {
        when(roomService.updateRoom(any(Room.class), eq(3L))).thenReturn(null);

        mockMvc.perform(put("/api/room/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomName\":\"Room 3\",\"rowQuantity\":5,\"seatQuantity\":50}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_room() throws Exception {
        doNothing().when(roomService).deleteRoom(2L);

        mockMvc.perform(delete("/api/room/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_not_found_for_delete_when_room_does_not_exist() throws Exception {
        doThrow(new RuntimeException("Room not found")).when(roomService).deleteRoom(3L);

        mockMvc.perform(delete("/api/room/3"))
                .andExpect(status().isNotFound());
    }
}
