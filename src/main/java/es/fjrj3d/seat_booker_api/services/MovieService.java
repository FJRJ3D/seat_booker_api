package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
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

    public Movie getMovieById(Long id) {
        return iMovieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with ID: " + id));
    }

    public Movie updateMovie(Movie movie, Long id) {
        if (!iMovieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }
        movie.setId(id);
        return iMovieRepository.save(movie);
    }

    public Movie updateMoviePartial(Movie movie, Long id) {
        Optional<Movie> existingMovieOpt = iMovieRepository.findById(id);
        if (existingMovieOpt.isEmpty()) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }

        Movie existingMovie = existingMovieOpt.get();

        for (Field field : Movie.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(movie);
                if (value != null) {
                    field.set(existingMovie, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return iMovieRepository.save(existingMovie);
    }

    public boolean deleteMovie(Long id) {
        if (!iMovieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with ID: " + id);
        }else {
            iMovieRepository.deleteById(id);
            return true;
        }
    }

    @Transactional
    public void deleteMoviesByIds(List<Long> movieIds) {
        if (movieIds == null || movieIds.isEmpty()) {
            throw new IllegalArgumentException("Movie IDs cannot be null or empty");
        }

        List<Movie> movies = iMovieRepository.findAllById(movieIds);

        if (movies.size() != movieIds.size()) {
            throw new MovieNotFoundException("Some movies not found");
        }

        iMovieRepository.deleteAll(movies);
    }
}
