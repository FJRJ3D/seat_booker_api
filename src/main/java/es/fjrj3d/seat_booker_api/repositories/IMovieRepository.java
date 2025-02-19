package es.fjrj3d.seat_booker_api.repositories;

import es.fjrj3d.seat_booker_api.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IMovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    @Query("SELECT m.title FROM Movie m")
    List<String> getAllMoviesTitles();
}
