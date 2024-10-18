package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping(path = "/post")
    public Movie createMovie(@RequestBody Movie movie){
        return movieService.createMovie(movie);
    }

    @GetMapping(path = "/get")
    public List<Movie> getAllMovies(){
        return movieService.getAllMovies();
    }

    @PutMapping(path = "/put/{id}")
    public Movie updateMovie(@RequestBody Movie movie, @PathVariable Long id){
        return movieService.updateMovie(movie, id);
    }

    @DeleteMapping(path = "/delete/{id}")
    public void deleteMovie(@PathVariable Long id){
        movieService.deleteMovie(id);
    }
}
