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
    public Room createMovie(@RequestBody Room room){
        return roomService.createMovie(room);
    }

    @GetMapping
    public List<Room> getAllMovies(){
        return roomService.getAllMovies();
    }

    @GetMapping(path = "/{id}")
    public Optional<Room> getMovieById(@PathVariable Long id){
        return roomService.getMovieById(id);
    }

    @PutMapping(path = "/{id}")
    public Room updateMovie(@RequestBody Room room, @PathVariable Long id){
        return roomService.updateMovie(room, id);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteMovie(@PathVariable Long id){
        roomService.deleteMovie(id);
    }
}
