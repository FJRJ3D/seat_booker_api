package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.RoomAlreadyExistsException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    IRoomRepository iRoomRepository;

    @Autowired
    IMovieRepository iMovieRepository;

    public Room createRoom(Room room, String movieTitle) {
        iRoomRepository.findByRoomName(room.getRoomName()).ifPresent(existingRoom -> {
            throw new RoomAlreadyExistsException("Room already exists with name: " + room.getRoomName());
        });

        Movie movie = iMovieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with title: " + movieTitle));

        room.setMovie(movie);
        return iRoomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return iRoomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return iRoomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));
    }

    public List<Room> getRoomsByMovieTitle(String movieTitle) {
        Movie movie = iMovieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with title: " + movieTitle));

        return iRoomRepository.findByMovieId(movie.getId());
    }

    public Room updateRoom(Room room, Long id) {
        if (!iRoomRepository.existsById(id)) {
            throw new RoomNotFoundException("Room not found with id: " + id);
        }
        room.setId(id);
        return iRoomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        if (!iRoomRepository.existsById(id)) {
            throw new RoomNotFoundException("Room not found with id: " + id);
        }
        iRoomRepository.deleteById(id);
    }
}
