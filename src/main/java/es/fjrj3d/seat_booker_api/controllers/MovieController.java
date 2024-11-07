package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    MovieService movieService;

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(201).body(createdMovie);
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Movie> updateMovie(@Valid @RequestBody Movie movie, @PathVariable Long id) {
        Movie updatedMovie = movieService.updateMovie(movie, id);
        return ResponseEntity.ok(updatedMovie);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Movie> updateMoviePartial(@RequestBody Movie movie, @PathVariable Long id) {
        Movie updatedMovie = movieService.updateMoviePartial(movie, id);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieService.deleteMovie(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMovies(@RequestBody List<Long> movieIds) {
        try {
            movieService.deleteMoviesByIds(movieIds);
            return ResponseEntity.ok("Movies deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<String> handleMovieNotFoundException(MovieNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessages = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errorMessages.append(error.getDefaultMessage()).append("\n");
        });
        return ResponseEntity.badRequest().body(errorMessages.toString());
    }
}
