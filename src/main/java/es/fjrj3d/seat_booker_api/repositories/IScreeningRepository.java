package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IScreeningRepository extends JpaRepository<Screening, Long> {
}
