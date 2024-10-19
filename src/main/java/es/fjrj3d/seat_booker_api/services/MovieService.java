package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private IMovieRepository iMovieRepository;

    public Movie createMovie(Movie movie){
        return iMovieRepository.save(movie);
    }

    public List<Movie> getAllMovies(){
        return iMovieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id){
        return iMovieRepository.findById(id);
    }

    public Movie updateMovie(Movie movie, Long id){
        movie.setId(id);
        return iMovieRepository.save(movie);
    }

    public void deleteMovie(Long id){
        iMovieRepository.deleteById(id);
    }
}
