package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.ollama.OllamaChatModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    @Mock
    private IMovieRepository iMovieRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private TmdbService tmdbService;

    @Mock
    private OllamaChatModel chatModel;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatModel.call(any(String.class))).thenReturn("rewritten synopsis");
        when(iMovieRepository.save(any(Movie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void test_create_movie() {
        Map<String, Object> fakeMovieMap = new HashMap<>();
        fakeMovieMap.put("title", "Fake Title");
        fakeMovieMap.put("overview", "Fake overview");
        fakeMovieMap.put("genre_ids", Arrays.asList(28, 12));
        fakeMovieMap.put("id", 101);
        fakeMovieMap.put("poster_path", "/fakeposter.jpg");
        fakeMovieMap.put("release_date", "2023-01-01");

        List<Map<String, Object>> fakeMoviesList = new ArrayList<>();
        fakeMoviesList.add(fakeMovieMap);
        Map<String, Object> fakeTmdbResponse = new HashMap<>();
        fakeTmdbResponse.put("results", fakeMoviesList);

        when(tmdbService.getNowPlayingMovies()).thenReturn(fakeTmdbResponse);
        when(tmdbService.getGenreNamesByIds(Arrays.asList(28, 12)))
                .thenReturn(Arrays.asList("Acción", "Aventura"));
        when(tmdbService.getMovieAgeRatingById(101L)).thenReturn("18");
        when(tmdbService.getMovieDuration(101)).thenReturn(120);
        when(tmdbService.convertIntegerToLocalTime(120))
                .thenReturn(LocalTime.of(2, 0));
        when(tmdbService.convertToLocalDate("2023-01-01"))
                .thenReturn(LocalDate.of(2023, 1, 1));

        Movie result = movieService.createMovie(0);
        assertEquals("Fake Title", result.getTitle());
        assertEquals("rewritten synopsis", result.getSynopsis());
        assertEquals(Arrays.asList("Acción", "Aventura"), result.getGenre());
        assertEquals("18", result.getAgeRating());
        assertEquals("https://image.tmdb.org/t/p/w1280/fakeposter.jpg", result.getCoverImageUrl());
        assertEquals(LocalTime.of(2, 0), result.getDuration());
        assertEquals(LocalDate.of(2023, 1, 1), result.getPremiere());
        verify(roomService).createRoom(any(Movie.class));
    }

    @Test
    public void test_create_movie_list() {
        List<Map<String, Object>> fakeMoviesList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Object> movieMap = new HashMap<>();
            movieMap.put("title", "Title " + i);
            movieMap.put("overview", "Overview " + i);
            movieMap.put("genre_ids", Arrays.asList(28, 12));
            movieMap.put("id", 100 + i);
            movieMap.put("poster_path", "/poster" + i + ".jpg");
            movieMap.put("release_date", "2023-01-0" + ((i % 9) + 1));
            fakeMoviesList.add(movieMap);
        }
        Map<String, Object> fakeTmdbResponse = new HashMap<>();
        fakeTmdbResponse.put("results", fakeMoviesList);
        when(tmdbService.getNowPlayingMovies()).thenReturn(fakeTmdbResponse);
        when(tmdbService.getGenreNamesByIds(Arrays.asList(28, 12)))
                .thenReturn(Arrays.asList("Acción", "Aventura"));
        when(tmdbService.getMovieAgeRatingById(anyLong())).thenReturn("18");
        when(tmdbService.getMovieDuration(anyInt())).thenReturn(120);
        when(tmdbService.convertIntegerToLocalTime(120))
                .thenReturn(LocalTime.of(2, 0));
        when(tmdbService.convertToLocalDate(anyString()))
                .thenReturn(LocalDate.of(2023, 1, 1));

        movieService.createMovieList();
        verify(iMovieRepository, times(15)).save(any(Movie.class));
        verify(roomService, times(15)).createRoom(any(Movie.class));
    }

    @Test
    public void test_get_all_movies() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie());
        movies.add(new Movie());
        when(iMovieRepository.findAll()).thenReturn(movies);
        List<Movie> result = movieService.getAllMovies();
        assertEquals(movies, result);
    }

    @Test
    public void test_get_all_movies_titles() {
        List<String> titles = Arrays.asList("Movie1", "Movie2");
        when(iMovieRepository.getAllMoviesTitles()).thenReturn(titles);
        List<String> result = movieService.getAllMoviesTitles();
        assertEquals(titles, result);
    }

    @Test
    public void test_get_movie_by_id_found() {
        Movie movie = new Movie();
        movie.setTitle("Found Movie");
        when(iMovieRepository.findById(1L)).thenReturn(Optional.of(movie));
        Movie result = movieService.getMovieById(1L);
        assertEquals("Found Movie", result.getTitle());
    }

    @Test
    public void test_get_movie_by_id_not_found() {
        when(iMovieRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            movieService.getMovieById(1L);
        });
        assertEquals("Movie not found with ID: 1", exception.getMessage());
    }

    @Test
    public void test_update_movie() {
        Movie existingMovie = new Movie();
        existingMovie.setTitle("Old Title");
        when(iMovieRepository.findById(1L)).thenReturn(Optional.of(existingMovie));

        Movie updateData = new Movie();
        updateData.setTitle("New Title");
        updateData.setSynopsis("New Synopsis");

        Movie updatedMovie = movieService.updateMovie(updateData, 1L);
        assertEquals("New Title", updatedMovie.getTitle());
        assertEquals("New Synopsis", updatedMovie.getSynopsis());
    }

    @Test
    public void test_delete_movie_found() {
        when(iMovieRepository.existsById(1L)).thenReturn(true);
        String result = movieService.deleteMovie(1L);
        assertEquals("Movie was successfully deleted", result);
        verify(iMovieRepository).deleteById(1L);
    }

    @Test
    public void test_delete_movie_not_found() {
        when(iMovieRepository.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            movieService.deleteMovie(1L);
        });
        assertEquals("Movie not found with ID: 1", exception.getMessage());
    }

    @Test
    public void test_delete_movies_by_ids_success() {
        List<Long> movieIds = Arrays.asList(1L, 2L, 3L);
        List<Movie> movies = Arrays.asList(new Movie(), new Movie(), new Movie());
        when(iMovieRepository.findAllById(movieIds)).thenReturn(movies);
        String result = movieService.deleteMoviesByIds(movieIds);
        assertEquals("Movies were successfully deleted", result);
        verify(iMovieRepository).deleteAll(movies);
    }

    @Test
    public void test_delete_movies_by_ids_not_all_found() {
        List<Long> movieIds = Arrays.asList(1L, 2L, 3L);
        List<Movie> movies = Arrays.asList(new Movie(), new Movie());
        when(iMovieRepository.findAllById(movieIds)).thenReturn(movies);
        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            movieService.deleteMoviesByIds(movieIds);
        });
        assertEquals("Some movies not found", exception.getMessage());
    }

    @Test
    public void test_delete_all_movies() {
        String result = movieService.deleteAllMovies();
        assertEquals("Movies were successfully deleted", result);
        verify(iMovieRepository).deleteAll();
    }
}