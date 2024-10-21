package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    IMovieRepository iMovieRepository;

    public Movie createMovie(@Valid Movie movie) {
        return iMovieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return iMovieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return iMovieRepository.findById(id);
    }

    public Movie updateMovie(Movie movie, Long id) {
        if (!iMovieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }
        movie.setId(id);
        return iMovieRepository.save(movie);
    }

    public boolean deleteMovie(Long id) {
        if (!iMovieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }else {
            iMovieRepository.deleteById(id);
            return true;
        }
    }
}
