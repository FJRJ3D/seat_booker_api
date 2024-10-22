package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.services.ScreeningService;
import es.fjrj3d.seat_booker_api.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/screening")
public class ScreeningController {

    @Autowired
    ScreeningService screeningService;

    @Autowired
    SeatService seatService;

    @PostMapping(path = "/{roomName}")
    public Screening createScreening(@RequestBody Screening screening, @PathVariable String roomName) {
        Screening createdScreening = screeningService.createScreening(screening, roomName);
        seatService.createSeatsForScreening(createdScreening);
        return createdScreening;
    }
}
