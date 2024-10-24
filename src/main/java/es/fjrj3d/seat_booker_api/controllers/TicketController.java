package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.exceptions.TicketNotFoundException;
import es.fjrj3d.seat_booker_api.models.Ticket;
import es.fjrj3d.seat_booker_api.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.status(201).body(createdTicket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Ticket> updateTicket(@Valid @RequestBody Ticket ticket, @PathVariable Long id) {
        Ticket updatedTicket = ticketService.updateTicket(ticket, id);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (ticketService.deleteTicket(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<String> handleTicketNotFoundException(TicketNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
