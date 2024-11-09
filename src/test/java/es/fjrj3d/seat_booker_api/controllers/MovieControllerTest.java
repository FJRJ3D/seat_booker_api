package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.exceptions.MovieNotFoundException;
import es.fjrj3d.seat_booker_api.models.EMovieAgeRating;
import es.fjrj3d.seat_booker_api.models.EMovieGenre;
import es.fjrj3d.seat_booker_api.models.EMovieUserRating;
import es.fjrj3d.seat_booker_api.models.Movie;
import es.fjrj3d.seat_booker_api.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    private Movie interstellar;
    private Movie titanic;

    private final List<Movie> movieList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();

        interstellar = new Movie();
        interstellar.setId(1L);
        interstellar.setTitle("Interstellar");
        interstellar.setSynopsis("Interstellar is a science fiction film directed by Christopher Nolan that explores " +
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
        titanic.setSynopsis("Titanic is a romantic drama directed by James Cameron, telling the story of Jack and " +
                "Rose.");
        titanic.setGenre(EMovieGenre.DRAMA);
        titanic.setAgeRating(EMovieAgeRating.EIGHTEEN_PLUS);
        titanic.setUserRating(EMovieUserRating.FIVE_STARS);
        titanic.setCoverImageUrl("https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg");
        titanic.setDuration(LocalTime.of(3, 15));
        titanic.setPremiere(LocalDate.of(1997, 12, 19));

        movieList.add(interstellar);
        movieList.add(titanic);
    }

    @Test
    void should_create_movie() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenReturn(interstellar);

        mockMvc.perform(post("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Interstellar\",\"synopsis\":\"Interstellar is a science fiction film " +
                                "directed by Christopher Nolan that explores themes of love.\",\"genre\":" +
                                "\"SCIENCE_FICTION\",\"ageRating\":\"SEVEN_PLUS\",\"userRating\":\"FIVE_STARS\"," +
                                "\"coverImageUrl\":\"https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X" +
                                "_400x400.jpeg\",\"duration\":\"02:49\",\"premiere\":\"07-11-2014\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Interstellar"))
                .andExpect(jsonPath("$.synopsis").value("Interstellar is a science fiction " +
                        "film directed by Christopher Nolan that explores themes of love."))
                .andExpect(jsonPath("$.genre").value("SCIENCE_FICTION"))
                .andExpect(jsonPath("$.ageRating").value("SEVEN_PLUS"))
                .andExpect(jsonPath("$.userRating").value("FIVE_STARS"))
                .andExpect(jsonPath("$.coverImageUrl").value("https://pbs.twimg.com/profile_" +
                        "images/558490159834857472/gpoC7V0X_400x400.jpeg"))
                .andExpect(jsonPath("$.duration").value("02:49"))
                .andExpect(jsonPath("$.premiere").value("07-11-2014"));
    }

    @Test
    void should_return_all_movies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(movieList);

        mockMvc.perform(get("/api/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Interstellar"))
                .andExpect(jsonPath("$[0].synopsis").value("Interstellar is a science fiction " +
                        "film directed by Christopher Nolan that explores themes of love."))
                .andExpect(jsonPath("$[0].genre").value("SCIENCE_FICTION"))
                .andExpect(jsonPath("$[0].ageRating").value("SEVEN_PLUS"))
                .andExpect(jsonPath("$[0].userRating").value("FIVE_STARS"))
                .andExpect(jsonPath("$[0].coverImageUrl").value("https://pbs.twimg.com/profile" +
                        "_images/558490159834857472/gpoC7V0X_400x400.jpeg"))
                .andExpect(jsonPath("$[0].duration").value("02:49"))
                .andExpect(jsonPath("$[0].premiere").value("07-11-2014"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Titanic"))
                .andExpect(jsonPath("$[1].synopsis").value("Titanic is a romantic drama " +
                        "directed by James Cameron, telling the story of Jack and Rose."))
                .andExpect(jsonPath("$[1].genre").value("DRAMA"))
                .andExpect(jsonPath("$[1].ageRating").value("EIGHTEEN_PLUS"))
                .andExpect(jsonPath("$[1].userRating").value("FIVE_STARS"))
                .andExpect(jsonPath("$[1].coverImageUrl").value("https://upload.wikimedia.org" +
                        "/wikipedia/en/2/22/Titanic_poster.jpg"))
                .andExpect(jsonPath("$[1].duration").value("03:15"))
                .andExpect(jsonPath("$[1].premiere").value("19-12-1997"));
    }

    @Test
    void should_return_movie_by_id() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(interstellar);

        mockMvc.perform(get("/api/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Interstellar"))
                .andExpect(jsonPath("$.synopsis").value("Interstellar is a science fiction " +
                        "film directed by Christopher Nolan that explores themes of love."))
                .andExpect(jsonPath("$.genre").value("SCIENCE_FICTION"))
                .andExpect(jsonPath("$.ageRating").value("SEVEN_PLUS"))
                .andExpect(jsonPath("$.userRating").value("FIVE_STARS"))
                .andExpect(jsonPath("$.coverImageUrl").value("https://pbs.twimg.com/profile_" +
                        "images/558490159834857472/gpoC7V0X_400x400.jpeg"))
                .andExpect(jsonPath("$.duration").value("02:49"))
                .andExpect(jsonPath("$.premiere").value("07-11-2014"));
    }

    @Test
    void should_return_not_found_for_nonexistent_movie() throws Exception {
        when(movieService.getMovieById(3L)).thenThrow(new MovieNotFoundException("Movie not found"));

        mockMvc.perform(get("/api/movie/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_movie() throws Exception {
        when(movieService.updateMovie(any(Movie.class), eq(2L))).thenReturn(titanic);

        mockMvc.perform(patch("/api/movie/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titanic\",\"synopsis\":\"Titanic is a romantic drama directed by James" +
                                " Cameron, telling the story of Jack and Rose.\",\"genre\":\"DRAMA\",\"ageRating\"" +
                                ":\"EIGHTEEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":\"https://upload" +
                                ".wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg\",\"duration\":\"03:15\",\"" +
                                "premiere\":\"19-12-1997\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titanic"))
                .andExpect(jsonPath("$.synopsis").value("Titanic is a romantic drama directed" +
                        " by James Cameron, telling the story of Jack and Rose."))
                .andExpect(jsonPath("$.genre").value("DRAMA"))
                .andExpect(jsonPath("$.ageRating").value("EIGHTEEN_PLUS"))
                .andExpect(jsonPath("$.userRating").value("FIVE_STARS"))
                .andExpect(jsonPath("$.coverImageUrl").value("https://upload.wikimedia.org/" +
                        "wikipedia/en/2/22/Titanic_poster.jpg"))
                .andExpect(jsonPath("$.duration").value("03:15"))
                .andExpect(jsonPath("$.premiere").value("19-12-1997"));
    }

    @Test
    void should_return_not_found_for_update_when_movie_does_not_exist() throws Exception {
        when(movieService.updateMovie(any(Movie.class), eq(3L))).thenThrow(new MovieNotFoundException("Movie" +
                " not found"));

        mockMvc.perform(patch("/api/movie/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titanic\",\"synopsis\":\"Titanic is a romantic drama directed by " +
                                "James Cameron, telling the story of Jack and Rose.\",\"genre\":\"DRAMA\",\"" +
                                "ageRating\":\"EIGHTEEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":\"" +
                                "https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg\",\"duration\":" +
                                "\"03:15\",\"premiere\":\"19-12-1997\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_movie() throws Exception {
        when(movieService.deleteMovie(2L)).thenReturn("Movie was successfully deleted");
        mockMvc.perform(delete("/api/movie/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie was successfully deleted"));
    }

    @Test
    void should_return_not_found_for_delete_when_movie_does_not_exist() throws Exception {
        doThrow(new MovieNotFoundException("Movie not found with ID: 3"))
                .when(movieService).deleteMovie(3L);
        mockMvc.perform(delete("/api/movie/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_movies() throws Exception {
        when(movieService.deleteMoviesByIds(anyList())).thenReturn("Movies were successfully deleted");

        mockMvc.perform(delete("/api/movie/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Movies were successfully deleted"));
    }

    @Test
    void should_return_bad_request_when_movie_ids_are_empty() throws Exception {
        when(movieService.deleteMoviesByIds(anyList())).thenThrow(new IllegalArgumentException("Movie IDs cannot be " +
                "null or empty"));

        mockMvc.perform(delete("/api/movie/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Movie IDs cannot be null or empty"));
    }

    @Test
    void should_return_bad_request_when_some_movies_not_found() throws Exception {
        when(movieService.deleteMoviesByIds(anyList())).thenThrow(new IllegalArgumentException("Some movies not " +
                "found"));

        mockMvc.perform(delete("/api/movie/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Some movies not found"));
    }
}
