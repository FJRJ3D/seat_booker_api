package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.services.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    RoomService roomService;

//    @PostMapping(path = "/{movieTitle}")
//    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room, @PathVariable String movieTitle) {
//        Room createdRoom = roomService.createRoom(room, movieTitle);
//        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
//    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @GetMapping(path = "/movie/{movieTitle}")
    public ResponseEntity<List<Room>> getRoomsByMovieTitle(@PathVariable String movieTitle) {
        List<Room> rooms = roomService.getRoomsByMovieTitle(movieTitle);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room, @PathVariable Long id) {
        Room updatedRoom = roomService.updateRoom(room, id);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        String resultMessage = roomService.deleteRoom(id);
        return ResponseEntity.ok(resultMessage);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRooms(@RequestBody List<Long> roomIds) {
        try {
            String resultMessage = roomService.deleteRoomsByIds(roomIds);
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
