package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.ScreeningNotFoundException;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.repositories.IScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreeningService {

    @Autowired
    IScreeningRepository iScreeningRepository;

    @Autowired
    IRoomRepository iRoomRepository;

    public Screening createScreening(Screening screening, String roomName) {
        Room room = iRoomRepository.findByRoomName(roomName)
                .orElseThrow(() -> new ScreeningNotFoundException("Room not found with name: " +roomName));
        screening.setRoom(room);
        return iScreeningRepository.save(screening);
    }
}
