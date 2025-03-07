package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    MovieService movieService;

    @PostMapping
    public ResponseEntity<List<Movie>> createCinemaSchedule() {
        List<Movie> movieList = movieService.createMovieList();
        return ResponseEntity.status(HttpStatus.CREATED).body(movieList);
    }

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

    @DeleteMapping ("/all")
    public String deleteAllMovies (){
        return movieService.deleteAllMovies();
    }
}
