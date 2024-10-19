package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.models.Room;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private IRoomRepository iRoomRepository;

    public Room createMovie(Room room){
        return iRoomRepository.save(room);
    }

    public List<Room> getAllMovies(){
        return iRoomRepository.findAll();
    }

    public Optional<Room> getMovieById(Long id){
        return iRoomRepository.findById(id);
    }

    public Room updateMovie(Room room, Long id){
        room.setId(id);
        return iRoomRepository.save(room);
    }

    public void deleteMovie(Long id){
        iRoomRepository.deleteById(id);
    }
}
