package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.EMovieAgeRating;
import es.fjrj3d.seat_booker_api.models.EMovieGenre;
import es.fjrj3d.seat_booker_api.models.EMovieUserRating;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
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
        interstellar.setSynopsis("Interstellar is a science fiction film directed by Christopher Nolan that explores themes of love.");
        interstellar.setGenre(EMovieGenre.SCIENCE_FICTION);
        interstellar.setAgeRating(EMovieAgeRating.SEVEN_PLUS);
        interstellar.setUserRating(EMovieUserRating.FIVE_STARS);
        interstellar.setCoverImageUrl("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg");
        interstellar.setDuration(Duration.ofMinutes(169));
        interstellar.setPremiere(LocalDate.of(2014, 11, 7));

        titanic = new Movie();
        titanic.setId(2L);
        titanic.setTitle("Titanic");
        titanic.setSynopsis("Titanic is a romantic drama directed by James Cameron, telling the story of Jack and Rose.");
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
    void should_create_movie() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenReturn(interstellar);

        mockMvc.perform(post("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Interstellar\",\"synopsis\":\"Interstellar is a science fiction film directed by Christopher Nolan that explores themes of love.\",\"genre\":\"SCIENCE_FICTION\",\"ageRating\":\"SEVEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":\"https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg\",\"duration\":\"PT2H49M\",\"premiere\":\"2014-11-07\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Interstellar"));
    }

    @Test
    void should_return_all_movies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(movieList);

        mockMvc.perform(get("/api/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Interstellar"))
                .andExpect(jsonPath("$[1].title").value("Titanic"));
    }

    @Test
    void should_return_movie_by_id() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(interstellar));

        mockMvc.perform(get("/api/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Interstellar"));
    }

    @Test
    void should_return_not_found_for_nonexistent_movie() throws Exception {
        when(movieService.getMovieById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movie/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_movie() throws Exception {
        when(movieService.updateMovie(any(Movie.class), eq(2L))).thenReturn(titanic);

        mockMvc.perform(put("/api/movie/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titanic\",\"synopsis\":\"Titanic is a romantic drama directed by James Cameron, telling the story of Jack and Rose.\",\"genre\":\"DRAMA\",\"ageRating\":\"EIGHTEEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg\",\"duration\":\"PT3H15M\",\"premiere\":\"1997-12-19\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titanic"));
    }

    @Test
    void should_return_not_found_for_update_when_movie_does_not_exist() throws Exception {
        when(movieService.updateMovie(any(Movie.class), eq(3L))).thenThrow(new MovieNotFoundException("Movie not found"));

        mockMvc.perform(put("/api/movie/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titanic\",\"synopsis\":\"Titanic is a romantic drama directed by James Cameron, telling the story of Jack and Rose.\",\"genre\":\"DRAMA\",\"ageRating\":\"EIGHTEEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg\",\"duration\":\"PT3H15M\",\"premiere\":\"1997-12-19\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_movie() throws Exception {
        when(movieService.deleteMovie(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/movie/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_not_found_for_delete_when_movie_does_not_exist() throws Exception {
        when(movieService.deleteMovie(3L)).thenReturn(false);

        mockMvc.perform(delete("/api/movie/3"))
                .andExpect(status().isNotFound());
    }
}
