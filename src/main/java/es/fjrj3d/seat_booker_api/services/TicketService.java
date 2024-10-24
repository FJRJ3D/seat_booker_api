package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.repositories.ITicketRepository;
import es.fjrj3d.seat_booker_api.exceptions.TicketNotFoundException;
import es.fjrj3d.seat_booker_api.models.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    ITicketRepository iTicketRepository;

    public Ticket createTicket(@Valid Ticket ticket) {
        return iTicketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return iTicketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return iTicketRepository.findById(id);
    }

    public Ticket updateTicket(Ticket ticket, Long id) {
        if (!iTicketRepository.existsById(id)) {
            throw new TicketNotFoundException("Ticket not found with ID: " + id);
        }
        ticket.setId(id);
        return iTicketRepository.save(ticket);
    }

    public boolean deleteTicket(Long id) {
        if (!iTicketRepository.existsById(id)) {
            throw new TicketNotFoundException("Ticket not found with ID: " + id);
        } else {
            iTicketRepository.deleteById(id);
            return true;
        }
    }
}
