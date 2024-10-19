package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public Room createRoom(@RequestBody Room room){
        return roomService.createRoom(room);
    }

    @GetMapping
    public List<Room> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping(path = "/{id}")
    public Optional<Room> getRoomById(@PathVariable Long id){
        return roomService.getRoomById(id);
    }

    @PutMapping(path = "/{id}")
    public Room updateRoom(@RequestBody Room room, @PathVariable Long id){
        return roomService.updateRoom(room, id);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteRoom(@PathVariable Long id){
        roomService.deleteRoom(id);
    }
}
