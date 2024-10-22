package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.models.Screening;
import es.fjrj3d.seat_booker_api.models.Seat;
import es.fjrj3d.seat_booker_api.repositories.ISeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    @Autowired
    ISeatRepository iSeatRepository;

    public void createSeatsForScreening(Screening screening) {
        int rowQuantity = screening.getRowQuantity();
        int seatQuantity = screening.getSeatQuantity();
        List<Seat> seats = new ArrayList<>();
        char[] rowLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        for (int row = 1; row <= rowQuantity; row++) {
            char rowLetter = row <= rowLetters.length ? rowLetters[row - 1] : '?';

            for (int seat = 1; seat <= seatQuantity; seat++) {
                Seat newSeat = new Seat();
                newSeat.setRow(row);
                newSeat.setSeat(seat);
                newSeat.setSeatName("Seat " + rowLetter + "-" + seat);
                newSeat.setScreening(screening);
                seats.add(newSeat);
            }
        }

        screening.setSeats(seats);
        iSeatRepository.saveAll(seats);
    }
}
