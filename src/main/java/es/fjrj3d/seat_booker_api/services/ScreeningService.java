package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.ScreeningNotFoundException;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.repositories.IScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Screening> getAllScreenings(){
        return iScreeningRepository.findAll();
    }

    public Optional<Screening> getScreeningById(Long id) {
        return iScreeningRepository.findById(id);
    }

    public List<Screening> getAllScreeningsByRoom(String roomName) {
        Room room = iRoomRepository.findByRoomName(roomName)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with name: " + roomName));

        return iScreeningRepository.findByRoomId(room.getId());
    }

    public Screening updateScreening(Screening screening, Long id) {
        screening.setId(id);
        return iScreeningRepository.save(screening);
    }

    public boolean deleteScreening(Long id) {
        if (!iScreeningRepository.existsById(id)) {
            throw new ScreeningNotFoundException("Screening not found with ID: " + id);
        } else {
            iScreeningRepository.deleteById(id);
            return true;
        }
    }
}
