package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie){
        return movieService.createMovie(movie);
    }

    @GetMapping
    public List<Movie> getAllMovies(){
        return movieService.getAllMovies();
    }

    @GetMapping(path = "/{id}")
    public Optional<Movie> getMovieById(@PathVariable Long id){
        return movieService.getMovieById(id);
    }

    @PutMapping(path = "/{id}")
    public Movie updateMovie(@RequestBody Movie movie, @PathVariable Long id){
        return movieService.updateMovie(movie, id);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteMovie(@PathVariable Long id){
        movieService.deleteMovie(id);
    }
}
