package es.fjrj3d.seat_booker_api.exceptions;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String message) {
        super(message);
    }
}
