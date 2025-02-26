package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.ScreeningNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.repositories.IScreeningRepository;
import es.fjrj3d.seat_booker_api.utils.DurationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScreeningService {

    @Autowired
    IScreeningRepository iScreeningRepository;

    @Autowired
    SeatService seatService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    IRoomRepository iRoomRepository;

    @Autowired
    PaymentService paymentService;

    Screening screening;

    public void createScreening(Room room, Movie movie) {
        screening = new Screening();

        LocalTime openingTime = LocalTime.of(12, 30);
        LocalTime closingTime = LocalTime.of(23, 59);

        Duration movieDuration = Duration.between(LocalTime.MIN, movie.getDuration());
        int totalMinutes = (int) movieDuration.toMinutes();
        int maxSessions = (int) Duration.between(openingTime, closingTime).toMinutes() / (totalMinutes + 10);

        LocalTime startTime = openingTime;

        for (int i = 0; i<maxSessions; i++){
            if (startTime.plusMinutes(totalMinutes).isAfter(closingTime)) {
                break;
            }

            screening = new Screening();
            screening.setSchedule(startTime);
            screening.setDuration(Duration.between(LocalTime.MIN, movie.getDuration()));

            screening.setRoom(room);
            iScreeningRepository.save(screening);

            seatService.createSeatsForScreening(screening, room);

            startTime = startTime.plusMinutes(totalMinutes + 10);
        }
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
        LocalDateTime screeningDateTime = screening.getSchedule().atDate(LocalDate.now());
        Duration timeElapsed = Duration.between(screeningDateTime, now);
        return screening.getDuration().minus(timeElapsed);
    }

    public Duration calculateTimeUntilScreening(Screening screening) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime screeningDateTime = screening.getSchedule().atDate(LocalDate.now());
        return Duration.between(now, screeningDateTime);
    }

    @Scheduled(fixedRate = 1000)
    public void sendDurationUpdates() {
        List<Screening> screenings = iScreeningRepository.findAll();

        for (Screening screening : screenings) {
            Duration remaining = calculateRemainingDuration(screening);
            Duration timeUntilScreening = calculateTimeUntilScreening(screening);
            String message;

            if (timeUntilScreening.isNegative() || timeUntilScreening.isZero()) {
                if (!remaining.isNegative() && !remaining.isZero()) {
                    if (paymentService.areAllSeatsReserved(screening.getId())){
                        message = "Remaining duration: " + DurationUtils.formatDuration(remaining);
                        webSocketService.sendDurationUpdate(screening.getId(), message);
                        updateAvailability(screening.getId(), false);
                    } else {
                        message = "Remaining duration: " + DurationUtils.formatDuration(remaining);
                        webSocketService.sendDurationUpdate(screening.getId(), message);
                        updateAvailability(screening.getId(), true);
                    }
                } else {
                    webSocketService.sendScreeningEnded(screening.getId());
                    updateAvailability(screening.getId(), false);
                }
            } else {
                if (paymentService.areAllSeatsReserved(screening.getId())){
                    message = "Time until screening: " + DurationUtils.formatDuration(timeUntilScreening);
                    webSocketService.sendDurationUpdate(screening.getId(), message);
                    updateAvailability(screening.getId(), false);
                } else {
                    message = "Time until screening: " + DurationUtils.formatDuration(timeUntilScreening);
                    webSocketService.sendDurationUpdate(screening.getId(), message);
                    updateAvailability(screening.getId(), true);
                }
            }
        }
    }

    private void updateAvailability(Long screeningId, boolean availability) {
        Optional<Screening> optionalScreening = iScreeningRepository.findById(screeningId);
        if (optionalScreening.isPresent()) {
            Screening screeningToUpdate = optionalScreening.get();
            screeningToUpdate.setAvailability(availability);
            iScreeningRepository.save(screeningToUpdate);
        }
    }
}
