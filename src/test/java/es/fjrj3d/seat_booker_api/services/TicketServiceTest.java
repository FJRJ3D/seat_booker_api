package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.TicketNotFoundException;
import es.fjrj3d.seat_booker_api.models.Ticket;
import es.fjrj3d.seat_booker_api.repositories.ITicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {

    @Mock
    private ITicketRepository iTicketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private Long ticketId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ticket = new Ticket();
        ticket.setId(ticketId);
    }

    @Test
    public void test_create_ticket() {
        when(iTicketRepository.save(ticket)).thenReturn(ticket);

        Ticket createdTicket = ticketService.createTicket(ticket);

        assertEquals(ticket, createdTicket);
        verify(iTicketRepository, times(1)).save(ticket);
    }

    @Test
    public void test_get_all_tickets() {
        when(iTicketRepository.findAll()).thenReturn(List.of(ticket));

        List<Ticket> tickets = ticketService.getAllTickets();

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(iTicketRepository, times(1)).findAll();
    }

    @Test
    public void test_get_ticket_by_id_found() {
        when(iTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Optional<Ticket> foundTicket = ticketService.getTicketById(ticketId);

        assertTrue(foundTicket.isPresent());
        assertEquals(ticket, foundTicket.get());
        verify(iTicketRepository, times(1)).findById(ticketId);
    }

    @Test
    public void test_get_ticket_by_id_not_found() {
        when(iTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        Optional<Ticket> foundTicket = ticketService.getTicketById(ticketId);

        assertFalse(foundTicket.isPresent());
        verify(iTicketRepository, times(1)).findById(ticketId);
    }

    @Test
    public void test_update_ticket_found() {
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(ticketId);
        when(iTicketRepository.existsById(ticketId)).thenReturn(true);
        when(iTicketRepository.save(updatedTicket)).thenReturn(updatedTicket);

        Ticket result = ticketService.updateTicket(updatedTicket, ticketId);

        assertEquals(updatedTicket, result);
        verify(iTicketRepository, times(1)).existsById(ticketId);
        verify(iTicketRepository, times(1)).save(updatedTicket);
    }

    @Test
    public void test_update_ticket_not_found() {
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(ticketId);
        when(iTicketRepository.existsById(ticketId)).thenReturn(false);

        TicketNotFoundException thrown = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.updateTicket(updatedTicket, ticketId);
        });

        assertEquals("Ticket not found with ID: " + ticketId, thrown.getMessage());
        verify(iTicketRepository, times(1)).existsById(ticketId);
    }

    @Test
    public void test_delete_ticket() {
        when(iTicketRepository.existsById(ticketId)).thenReturn(true);

        boolean result = ticketService.deleteTicket(ticketId);

        assertTrue(result);
        verify(iTicketRepository, times(1)).existsById(ticketId);
        verify(iTicketRepository, times(1)).deleteById(ticketId);
    }

    @Test
    public void test_delete_ticket_not_found() {
        when(iTicketRepository.existsById(ticketId)).thenReturn(false);

        TicketNotFoundException thrown = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.deleteTicket(ticketId);
        });

        assertEquals("Ticket not found with ID: " + ticketId, thrown.getMessage());
        verify(iTicketRepository, times(1)).existsById(ticketId);
    }
}