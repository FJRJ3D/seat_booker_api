package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.exceptions.RoomNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    IRoomRepository iRoomRepository;

    @Autowired
    IMovieRepository iMovieRepository;

    Room room;

    public Room createRoom(Movie movie) {
        room = new Room();

        int roomNumber = 1;
        while (iRoomRepository.existsByRoomName("Room " + roomNumber)) {
            roomNumber++;
        }

        String generatedRoomName = "Room " + roomNumber;
        room.setRoomName(generatedRoomName);

        seatQuantity(room, generatedRoomName);

        room.setMovie(movie);
        return iRoomRepository.save(room);
    }

    private static void seatQuantity(Room room, String generatedRoomName) {
        if (generatedRoomName.equals("Room 1") ||
                generatedRoomName.equals("Room 2") ||
                generatedRoomName.equals("Room 3")) {
            room.setRowQuantity(10);
            room.setSeatQuantity(12);
        }

        if (generatedRoomName.equals("Room 4") ||
                generatedRoomName.equals("Room 5") ||
                generatedRoomName.equals("Room 6") ||
                generatedRoomName.equals("Room 7") ||
                generatedRoomName.equals("Room 8")){
            room.setRowQuantity(8);
            room.setSeatQuantity(9);
        }

        if (generatedRoomName.equals("Room 9") ||
                generatedRoomName.equals("Room 10") ||
                generatedRoomName.equals("Room 11") ||
                generatedRoomName.equals("Room 12") ||
                generatedRoomName.equals("Room 13")){
            room.setRowQuantity(6);
            room.setSeatQuantity(10);
        }

        if (generatedRoomName.equals("Room 14") ||
                generatedRoomName.equals("Room 15")){
            room.setRowQuantity(5);
            room.setSeatQuantity(8);
        }
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
        Optional<Room> existingRoomOpt = iRoomRepository.findById(id);
        if (existingRoomOpt.isEmpty()) {
            throw new RoomNotFoundException("Room not found with ID: " + id);
        }

        Room existingRoom = existingRoomOpt.get();

        if (room.getRoomName() != null) {
            existingRoom.setRoomName(room.getRoomName());
        }
        if (room.getRowQuantity() != null) {
            existingRoom.setRowQuantity(room.getRowQuantity());
        }
        if (room.getSeatQuantity() != null) {
            existingRoom.setSeatQuantity(room.getSeatQuantity());
        }
        if (room.getMovie() != null) {
            existingRoom.setMovie(room.getMovie());
        }
        if (room.getScreenings() != null) {
            existingRoom.setScreenings(room.getScreenings());
        }

        return iRoomRepository.save(existingRoom);
    }

    public String deleteRoom(Long id) {
        if (!iRoomRepository.existsById(id)) {
            throw new RoomNotFoundException("Room not found with id: " + id);
        }else {
            iRoomRepository.deleteById(id);
            return "Room was successfully deleted";
        }
    }

    public Room getRoomByName(String roomName) {
        return iRoomRepository.findByRoomName(roomName)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with name: " + roomName));
    }

    @Transactional
    public String deleteRoomsByIds(List<Long> roomIds) {
        if (roomIds == null || roomIds.isEmpty()) {
            throw new IllegalArgumentException("Room IDs cannot be null or empty");
        }

        List<Room> rooms = iRoomRepository.findAllById(roomIds);

        if (rooms.size() != roomIds.size()) {
            throw new RoomNotFoundException("Some rooms not found");
        }

        iRoomRepository.deleteAll(rooms);
        return "Rooms were successfully deleted";
    }
}
