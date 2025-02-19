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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private IMovieRepository iMovieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie interstellar;
    private Movie titanic;

    private final List<Movie> movieList = new ArrayList<>();
    private final List<String> movieTitles = new ArrayList<>();

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
        interstellar.setDuration(LocalTime.of(2, 49));
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
        titanic.setDuration(LocalTime.of(3, 15));
        titanic.setPremiere(LocalDate.of(1997, 12, 19));

        movieList.add(interstellar);
        movieList.add(titanic);

        movieTitles.add(interstellar.getTitle());
        movieTitles.add(titanic.getTitle());
    }

    @Test
    void should_create_movie_when_valid_data_is_provided() {
        when(iMovieRepository.save(any(Movie.class))).thenReturn(interstellar);

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
        assertEquals(LocalTime.of(2, 49), result.getDuration());
        assertEquals(LocalDate.of(2014, 11, 7), result.getPremiere());
        verify(iMovieRepository, times(1)).save(interstellar);
    }

    @Test
    void should_return_all_movies() {
        when(iMovieRepository.findAll()).thenReturn(movieList);

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
        assertEquals(LocalTime.of(2, 49), interstellarResult.getDuration());
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
        assertEquals(LocalTime.of(3, 15), titanicResult.getDuration());
        assertEquals(LocalDate.of(1997, 12, 19), titanicResult.getPremiere());

        verify(iMovieRepository, times(1)).findAll();
    }

    @Test
    void should_return_all_title_movies() {
        when(iMovieRepository.getAllMoviesTitles()).thenReturn(movieTitles);

        List<String> result = movieService.getAllMoviesTitles();

        assertEquals(2, result.size());
        assertEquals("Interstellar", result.get(0));
        assertEquals("Titanic", result.get(1));

        verify(iMovieRepository, times(1)).getAllMoviesTitles();
    }

    @Test
    void should_return_movie_by_id() {
        when(iMovieRepository.findById(1L)).thenReturn(Optional.of(interstellar));

        Movie result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Interstellar", result.getTitle());
        assertEquals("Interstellar is a science fiction film directed by Christopher Nolan that explores" +
                "themes of love.", result.getSynopsis());
        assertEquals(EMovieGenre.SCIENCE_FICTION, result.getGenre());
        assertEquals(EMovieAgeRating.SEVEN_PLUS, result.getAgeRating());
        assertEquals(EMovieUserRating.FIVE_STARS, result.getUserRating());
        assertEquals("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg",
                result.getCoverImageUrl());
        assertEquals(LocalTime.of(2, 49), result.getDuration());
        assertEquals(LocalDate.of(2014, 11, 7), result.getPremiere());

        verify(iMovieRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_when_movie_not_found_by_id() {
        when(iMovieRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(3L));

        verify(iMovieRepository, times(1)).findById(3L);
    }

    @Test
    void should_update_movie_when_movie_exists() {
        when(iMovieRepository.findById(1L)).thenReturn(Optional.of(interstellar));
        interstellar.setTitle("Interstellar Updated");
        when(iMovieRepository.save(interstellar)).thenReturn(interstellar);

        Movie result = movieService.updateMovie(interstellar, 1L);

        assertEquals("Interstellar Updated", result.getTitle());
        verify(iMovieRepository, times(1)).save(interstellar);
    }

    @Test
    void should_throw_exception_when_updating_non_existing_movie() {
        when(iMovieRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(titanic, 4L));
        verify(iMovieRepository, times(0)).save(any(Movie.class));
    }

    @Test
    void should_throw_exception_when_movie_not_found_for_deletion() {
        when(iMovieRepository.existsById(3L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(3L));
        verify(iMovieRepository, times(1)).existsById(3L);
    }

    @Test
    void should_delete_movie_when_exists() {
        when(iMovieRepository.existsById(2L)).thenReturn(true);

        String result = movieService.deleteMovie(2L);

        assertEquals("Movie was successfully deleted", result);
        verify(iMovieRepository, times(1)).deleteById(2L);
    }

    @Test
    void should_delete_movies_when_all_ids_exist() {
        when(iMovieRepository.findAllById(List.of(1L, 2L))).thenReturn(movieList);

        String result = movieService.deleteMoviesByIds(List.of(1L, 2L));

        assertEquals("Movies were successfully deleted", result);
        verify(iMovieRepository, times(1)).deleteAll(movieList);
    }

    @Test
    void should_throw_exception_when_any_movie_id_does_not_exist() {
        List<Long> idsToDelete = List.of(1L, 3L);
        when(iMovieRepository.findAllById(List.of(1L, 3L))).thenReturn(List.of(interstellar));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMoviesByIds(idsToDelete));

        verify(iMovieRepository, times(1)).findAllById(idsToDelete);
        verify(iMovieRepository, never()).deleteById(anyLong());
    }
}