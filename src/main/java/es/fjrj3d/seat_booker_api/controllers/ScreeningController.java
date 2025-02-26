package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.services.RoomService;
import es.fjrj3d.seat_booker_api.services.ScreeningService;
import es.fjrj3d.seat_booker_api.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/screening")
public class ScreeningController {

    @Autowired
    ScreeningService screeningService;

    @Autowired
    SeatService seatService;

    @Autowired
    RoomService roomService;

//    @PostMapping(path = "/{roomName}")
//    public Screening createScreening(@RequestBody Screening screening, @PathVariable String roomName) {
//        Screening createdScreening = screeningService.createScreening(screening, roomName);
//        Room room = roomService.getRoomByName(roomName);
//        seatService.createSeatsForScreening(createdScreening, room);
//        return createdScreening;
//    }

    @GetMapping
    public List<Screening> getAllScreenings() {
        return screeningService.getAllScreenings();
    }

    @GetMapping(path = "/{id}")
    public Optional<Screening> getScreeningById(@PathVariable Long id) {
        return screeningService.getScreeningById(id);
    }

    @GetMapping(path = "/room/{roomName}")
    public List<Screening> getAllScreeningsByRoom(@PathVariable String roomName) {
        return screeningService.getAllScreeningsByRoom(roomName);
    }

    @PutMapping(path = "/{id}")
    public Screening updateScreening(@RequestBody Screening screening, @PathVariable Long id) {
        return screeningService.updateScreening(screening, id);
    }

    @DeleteMapping(path = "/{id}")
    public boolean deleteScreening(@PathVariable Long id) {
        return screeningService.deleteScreening(id);
    }
}
