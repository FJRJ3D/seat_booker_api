package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.services.RoomService;
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

    @PostMapping(path = "/{movieTitle}")
    public ResponseEntity<Room> createRoom(@RequestBody Room room, @PathVariable String movieTitle) {
        Room createdRoom = roomService.createRoom(room, movieTitle);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

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

    @PutMapping(path = "/{id}")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room, @PathVariable Long id) {
        Room updatedRoom = roomService.updateRoom(room, id);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
