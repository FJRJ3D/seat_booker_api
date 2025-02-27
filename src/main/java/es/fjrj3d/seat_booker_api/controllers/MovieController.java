package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import es.fjrj3d.seat_booker_api.services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    MovieService movieService;

    private final TmdbService tmdbService;

    public MovieController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @PostMapping("/now")
    public List<Movie> setAllMovies (){
        return movieService.createMovies();
    }

//    @PostMapping
//    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
//        Movie createdMovie = movieService.createMovie(movie);
//        return ResponseEntity.status(201).body(createdMovie);
//    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/titles")
    public ResponseEntity<List<String>> getAllMoviesTitles() {
        List<String> movieTitles = movieService.getAllMoviesTitles();
        return ResponseEntity.ok(movieTitles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Movie> updateMovie(@RequestBody Movie movie, @PathVariable Long id) {
        Movie updatedMovie = movieService.updateMovie(movie, id);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        String resultMessage = movieService.deleteMovie(id);
        return ResponseEntity.ok(resultMessage);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMovies(@RequestBody List<Long> movieIds) {
        try {
            String resultMessage = movieService.deleteMoviesByIds(movieIds);
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping ("/now")
    public String deleteAllMovies (){
        return movieService.deleteAllMovies();
    }
}
