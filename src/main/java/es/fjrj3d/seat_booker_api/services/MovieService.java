package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.EMovieAgeRating;
import es.fjrj3d.seat_booker_api.models.EMovieGenre;
import es.fjrj3d.seat_booker_api.models.EMovieUserRating;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    IMovieRepository iMovieRepository;

    @Autowired
    OllamaChatModel chatModel;

    public Movie createMovie(Movie movie) {
        List<String> allMovies = getAllMoviesTitles();
        String movieTitle;

        do {
            movieTitle = chatModel.call("Escribeme el titulo de una pelicula que exista en la vida real. Solo pon " +
                    "el titulo, por ejemplo: Interstellar");
        } while (allMovies.contains(movieTitle));

        movie.setTitle(movieTitle);
        movie.setSynopsis(chatModel.call("Generame la sinopsis de la pelicula: " + movie.getTitle() + ", solo el" +
                " texto de la sinopsis, sin nada más, por ejemplo: Es una pelicula de ciencia ficcion donde..."));

        String genreString = chatModel.call("Tengo un enum con las siguientes generos: ACTION, ADVENTURE, COMEDY, DRAMA, FANTASY, HORROR, MYSTERY, ROMANTIC, SCIENCE_FICTION, THRILLER, ANIMATION, DOCUMENTARY, MUSICAL, CRIME, BIOGRAPHY, FAMILY, HISTORICAL, WAR, WESTERN, SPORT. Escribeme el que corresponde a " + movie.getTitle() + " escribemelo sin nada más, por ejemplo: ADVENTURE");
        try {
            EMovieGenre genre = EMovieGenre.valueOf(genreString.toUpperCase());
            movie.setGenre(genre);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El género generado no es válido: " + genreString);
        }

        String ageRatingString = chatModel.call("Tengo un enum con los siguientes ratings: ALL_AUDIENCES, SEVEN_PLUS, TWELVE_PLUS, SIXTEEN_PLUS, EIGHTEEN_PLUS. Escribeme el que corresponde a " + movie.getTitle() + " escribemelo sin nada más, por ejemplo: ALL_AUDIENCES");
        try {
            EMovieAgeRating ageRating = EMovieAgeRating.valueOf(ageRatingString.toUpperCase());
            movie.setAgeRating(ageRating);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El rating generado no es válido: " + ageRatingString);
        }

        movie.setUserRating(EMovieUserRating.ONE_STAR);

        String durationString = chatModel.call("Dame la duración de la pelicula: " + movie.getTitle() + " escribe solo la hora, por ejemplo: 2:10");
        String[] timeParts = durationString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        movie.setDuration(LocalTime.of(hours, minutes));

        String premiereString = chatModel.call("Dame el estreno de la pelicula: " + movie.getTitle() + " escribe solo la fecha, por ejemplo: 2014-11-07");
        String[] dateParts = premiereString.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        movie.setPremiere(LocalDate.of(year, month, day));

        return iMovieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return iMovieRepository.findAll();
    }

    public List<String> getAllMoviesTitles() {
        return iMovieRepository.getAllMoviesTitles();
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

        if (movie.getTitle() != null) {
            existingMovie.setTitle(movie.getTitle());
        }
        if (movie.getSynopsis() != null) {
            existingMovie.setSynopsis(movie.getSynopsis());
        }
        if (movie.getGenre() != null) {
            existingMovie.setGenre(movie.getGenre());
        }
        if (movie.getAgeRating() != null) {
            existingMovie.setAgeRating(movie.getAgeRating());
        }
        if (movie.getUserRating() != null) {
            existingMovie.setUserRating(movie.getUserRating());
        }
        if (movie.getCoverImageUrl() != null) {
            existingMovie.setCoverImageUrl(movie.getCoverImageUrl());
        }
        if (movie.getDuration() != null) {
            existingMovie.setDuration(movie.getDuration());
        }
        if (movie.getPremiere() != null) {
            existingMovie.setPremiere(movie.getPremiere());
        }
        if (movie.getRooms() != null) {
            existingMovie.setRooms(movie.getRooms());
        }
        if (movie.getReviews() != null) {
            existingMovie.setReviews(movie.getReviews());
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
