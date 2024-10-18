package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
}
