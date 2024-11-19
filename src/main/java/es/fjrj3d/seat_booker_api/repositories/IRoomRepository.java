package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByMovieId(Long movieId);
    Optional<Room> findByRoomName(String roomName);
    boolean existsByRoomName(String roomName);
}
