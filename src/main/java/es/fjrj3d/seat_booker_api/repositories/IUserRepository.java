package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
