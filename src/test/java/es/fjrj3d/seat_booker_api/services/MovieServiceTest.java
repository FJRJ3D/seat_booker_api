package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.EMovieAgeRating;
import es.fjrj3d.seat_booker_api.models.EMovieGenre;
import es.fjrj3d.seat_booker_api.models.EMovieUserRating;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie interstellar;
    private Movie titanic;

    private final List<Movie> movieList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        interstellar = new Movie();
        interstellar.setId(1L);
        interstellar.setTitle("Interstellar");
        interstellar.setSynopsis("Interstellar is a science fiction film directed by Christopher Nolan that explores" +
                "themes of love.");
        interstellar.setGenre(EMovieGenre.SCIENCE_FICTION);
        interstellar.setAgeRating(EMovieAgeRating.SEVEN_PLUS);
        interstellar.setUserRating(EMovieUserRating.FIVE_STARS);
        interstellar.setCoverImageUrl("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg");
        interstellar.setDuration(Duration.ofMinutes(169));
        interstellar.setPremiere(LocalDate.of(2014, 11, 7));

        titanic = new Movie();
        titanic.setId(2L);
        titanic.setTitle("Titanic");
        titanic.setSynopsis("Titanic is a romantic drama directed by James Cameron, telling the story of Jack and" +
                "Rose, two lovers from different social classes who meet aboard the ill-fated RMS Titanic.");
        titanic.setGenre(EMovieGenre.DRAMA);
        titanic.setAgeRating(EMovieAgeRating.EIGHTEEN_PLUS);
        titanic.setUserRating(EMovieUserRating.FIVE_STARS);
        titanic.setCoverImageUrl("https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg");
        titanic.setDuration(Duration.ofMinutes(195));
        titanic.setPremiere(LocalDate.of(1997, 12, 19));

        movieList.add(interstellar);
        movieList.add(titanic);
    }

    @Test
    void should_create_movie_when_valid_data_is_provided() {
        when(movieRepository.save(any(Movie.class))).thenReturn(interstellar);

        Movie result = movieService.createMovie(interstellar);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Interstellar", result.getTitle());
        assertEquals("Interstellar is a science fiction film directed by Christopher Nolan that explores" +
                "themes of love.", result.getSynopsis());
        assertEquals(EMovieGenre.SCIENCE_FICTION, result.getGenre());
        assertEquals(EMovieAgeRating.SEVEN_PLUS, result.getAgeRating());
        assertEquals(EMovieUserRating.FIVE_STARS, result.getUserRating());
        assertEquals("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg",
                result.getCoverImageUrl());
        assertEquals(Duration.ofMinutes(169), result.getDuration());
        assertEquals(LocalDate.of(2014, 11, 7), result.getPremiere());
        verify(movieRepository, times(1)).save(interstellar);
    }

    @Test
    void should_return_all_movies() {
        when(movieRepository.findAll()).thenReturn(movieList);

        List<Movie> result = movieService.getAllMovies();

        assertEquals(2, result.size());

        Movie interstellarResult = result.get(0);
        assertEquals(1L, interstellarResult.getId());
        assertEquals("Interstellar", interstellarResult.getTitle());
        assertEquals("Interstellar is a science fiction film directed by Christopher Nolan that explores" +
                "themes of love.", interstellarResult.getSynopsis());
        assertEquals(EMovieGenre.SCIENCE_FICTION, interstellarResult.getGenre());
        assertEquals(EMovieAgeRating.SEVEN_PLUS, interstellarResult.getAgeRating());
        assertEquals(EMovieUserRating.FIVE_STARS, interstellarResult.getUserRating());
        assertEquals("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg",
                interstellarResult.getCoverImageUrl());
        assertEquals(Duration.ofMinutes(169), interstellarResult.getDuration());
        assertEquals(LocalDate.of(2014, 11, 7), interstellarResult.getPremiere());

        Movie titanicResult = result.get(1);
        assertEquals(2L, titanicResult.getId());
        assertEquals("Titanic", titanicResult.getTitle());
        assertEquals("Titanic is a romantic drama directed by James Cameron, telling the story of Jack and" +
                "Rose, two lovers from different social classes who meet aboard the ill-fated RMS Titanic.",
                titanicResult.getSynopsis());
        assertEquals(EMovieGenre.DRAMA, titanicResult.getGenre());
        assertEquals(EMovieAgeRating.EIGHTEEN_PLUS, titanicResult.getAgeRating());
        assertEquals(EMovieUserRating.FIVE_STARS, titanicResult.getUserRating());
        assertEquals("https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg",
                titanicResult.getCoverImageUrl());
        assertEquals(Duration.ofMinutes(195), titanicResult.getDuration());
        assertEquals(LocalDate.of(1997, 12, 19), titanicResult.getPremiere());

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void should_return_movie_by_id() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(interstellar));

        Optional<Movie> result = movieService.getMovieById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Interstellar", result.get().getTitle());
        assertEquals("Interstellar is a science fiction film directed by Christopher Nolan that explores" +
                "themes of love.", result.get().getSynopsis());
        assertEquals(EMovieGenre.SCIENCE_FICTION, result.get().getGenre());
        assertEquals(EMovieAgeRating.SEVEN_PLUS, result.get().getAgeRating());
        assertEquals(EMovieUserRating.FIVE_STARS, result.get().getUserRating());
        assertEquals("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg",
                result.get().getCoverImageUrl());
        assertEquals(Duration.ofMinutes(169), result.get().getDuration());
        assertEquals(LocalDate.of(2014, 11, 7), result.get().getPremiere());

        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_when_movie_not_found_for_update() {
        when(movieRepository.existsById(3L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(interstellar, 3L));
        verify(movieRepository, times(1)).existsById(3L);
    }

    @Test
    void should_update_movie_when_exists() {
        when(movieRepository.existsById(2L)).thenReturn(true);
        when(movieRepository.save(titanic)).thenReturn(titanic);

        Movie result = movieService.updateMovie(titanic, 2L);

        assertEquals(2L, result.getId());
        assertEquals("Titanic", result.getTitle());
        assertEquals("Titanic is a romantic drama directed by James Cameron, telling the story of Jack and" +
                "Rose, two lovers from different social classes who meet aboard the ill-fated RMS Titanic.",
                result.getSynopsis());
        assertEquals(EMovieGenre.DRAMA, result.getGenre());
        assertEquals(EMovieAgeRating.EIGHTEEN_PLUS, result.getAgeRating());
        assertEquals(EMovieUserRating.FIVE_STARS, result.getUserRating());
        assertEquals("https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg",
                result.getCoverImageUrl());
        assertEquals(Duration.ofMinutes(195), result.getDuration());
        assertEquals(LocalDate.of(1997, 12, 19), result.getPremiere());

        verify(movieRepository, times(1)).save(titanic);
    }

    @Test
    void should_throw_exception_when_movie_not_found_for_deletion() {
        when(movieRepository.existsById(3L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(3L));
        verify(movieRepository, times(1)).existsById(3L);
    }

    @Test
    void should_delete_movie_when_exists() {
        when(movieRepository.existsById(2L)).thenReturn(true);

        boolean result = movieService.deleteMovie(2L);

        assertTrue(result);
        verify(movieRepository, times(1)).deleteById(2L);
    }
}