package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITicketRepository extends JpaRepository<Ticket, Long> {
}
