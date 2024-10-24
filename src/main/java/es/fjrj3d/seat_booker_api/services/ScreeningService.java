package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.ScreeningNotFoundException;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.repositories.IScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScreeningService {

    @Autowired
    IScreeningRepository iScreeningRepository;

    @Autowired
    WebSocketService webSocketService;

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

    public Duration calculateRemainingDuration(Screening screening) {
        LocalDateTime now = LocalDateTime.now();
        Duration timeElapsed = Duration.between(screening.getSchedule(), now);
        return screening.getDuration().minus(timeElapsed);
    }

    @Scheduled(fixedRate = 1000)
    public void sendDurationUpdates() {
        List<Screening> screenings = iScreeningRepository.findAll();

        for (Screening screening : screenings) {
            Duration remaining = calculateRemainingDuration(screening);

            if (!remaining.isNegative() && !remaining.isZero()) {
                webSocketService.sendDurationUpdate(screening.getId(), remaining);
                Optional<Screening> optionalScreening = iScreeningRepository.findById(screening.getId());
                if (optionalScreening.isPresent()) {
                    Screening screeningToUpdate = optionalScreening.get();
                    screeningToUpdate.setAvailability(true);
                    iScreeningRepository.save(screeningToUpdate);
                }
            } else {
                webSocketService.sendScreeningEnded(screening.getId());
                Optional<Screening> optionalScreening = iScreeningRepository.findById(screening.getId());
                if (optionalScreening.isPresent()) {
                    Screening screeningToUpdate = optionalScreening.get();
                    screeningToUpdate.setAvailability(false);
                    iScreeningRepository.save(screeningToUpdate);
                }
            }
        }
    }
}
