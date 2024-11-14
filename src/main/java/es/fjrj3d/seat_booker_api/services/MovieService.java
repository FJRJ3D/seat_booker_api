package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    IMovieRepository iMovieRepository;

    public Movie createMovie(Movie movie) {
        return iMovieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return iMovieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return iMovieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with ID: " + id));
    }

    public Movie updateMovie(Movie movie, Long id) {
        Optional<Movie> existingMovieOpt = iMovieRepository.findById(id);
        if (existingMovieOpt.isEmpty()) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }

        Movie existingMovie = existingMovieOpt.get();
        Logger logger = LoggerFactory.getLogger(MovieService.class);

        for (Field field : Movie.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(movie);
                if (value != null) {
                    field.set(existingMovie, value);
                }
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: {} when updating movie", field.getName(), e);
                throw new RuntimeException("Failed to update field " + field.getName(), e);
            }
        }

        return iMovieRepository.save(existingMovie);
    }

    public String deleteMovie(Long id) {
        if (!iMovieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }else {
            iMovieRepository.deleteById(id);
            return "Movie was successfully deleted";
        }
    }

    @Transactional
    public String deleteMoviesByIds(List<Long> movieIds) {
        if (movieIds == null || movieIds.isEmpty()) {
            throw new IllegalArgumentException("Movie IDs cannot be null or empty");
        }

        List<Movie> movies = iMovieRepository.findAllById(movieIds);

        if (movies.size() != movieIds.size()) {
            throw new MovieNotFoundException("Some movies not found");
        }

        iMovieRepository.deleteAll(movies);
        return "Movies were successfully deleted";
    }
}
