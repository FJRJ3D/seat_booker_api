package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISeatRepository extends JpaRepository<Seat, Long> {
}
