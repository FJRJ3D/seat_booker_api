package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    IMovieRepository iMovieRepository;

    @Autowired
    TmdbService tmdbService;

    @Autowired
    OllamaChatModel chatModel;

//    public Movie createMovie(Movie movie) {
//        movie.setSynopsis(chatModel.call("Generame la sinopsis de la pelicula: " + movie.getTitle() + ", solo " +
//                "el texto de la sinopsis, sin nada más, ni la palabra sipnosis ni nada"));
//        return iMovieRepository.save(movie);
//    }

    public List<Movie> createMovies() {
        List<Map<String, Object>> tmdbMovies = (List<Map<String, Object>>) tmdbService.getNowPlayingMovies().get("results");
        Movie movie;
        List<Movie> movieList = new ArrayList<>();

        for (int i = 0; i<15; i++){
            movie = new Movie();

            movie.setTitle((String) tmdbMovies.get(i).get("title"));

//            String synopsis = (String) tmdbMovies.get(i).get("overview");
//            movie.setSynopsis(chatModel.call("Esta es la synopsis de la pelicula: " + synopsis + ". Escibela de" +
//                    "nuevo con otras palabras en Español, por favor."));
            String synopsis = (String) tmdbMovies.get(i).get("overview");
            movie.setSynopsis(synopsis);

            List<String> genreListString = tmdbService.getGenreNamesByIds((List<Integer>) tmdbMovies.get(i).get("genre_ids"));
            movie.setGenre(genreListString);

            movie.setCoverImageUrl((String) tmdbMovies.get(i).get("poster_path"));

            movie.setPremiere(tmdbService.convertToLocalDate((String) tmdbMovies.get(i).get("release_date")));


            movieList.add(movie);
        }

        return iMovieRepository.saveAll(movieList);
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
