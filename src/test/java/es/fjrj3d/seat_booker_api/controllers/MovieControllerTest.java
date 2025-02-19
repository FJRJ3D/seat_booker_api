package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.dtos.request.RegisterRequest;
import es.fjrj3d.seat_booker_api.models.*;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import es.fjrj3d.seat_booker_api.services.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MovieControllerTest {

    @MockBean
    private OllamaChatModel chatModel;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMovieRepository iMovieRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;
    private Movie interstellar;
    private Movie titanic;
    private RegisterRequest registerRequest;

    private final List<Movie> movieList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE movie AUTO_INCREMENT = 1;");
        jdbcTemplate.execute("ALTER TABLE user AUTO_INCREMENT = 1;");

        registerRequest = new RegisterRequest("user@gmail.com", "user", "user");
        token = authService.register(registerRequest).accessToken();

        iMovieRepository.deleteAll();

        interstellar = new Movie();
        interstellar.setTitle("Interstellar");
        interstellar.setSynopsis("Interstellar is a science fiction film directed by Christopher Nolan that explores " +
                "themes of love.");
        interstellar.setGenre(EMovieGenre.SCIENCE_FICTION);
        interstellar.setAgeRating(EMovieAgeRating.SEVEN_PLUS);
        interstellar.setUserRating(EMovieUserRating.FIVE_STARS);
        interstellar.setCoverImageUrl("https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg");
        interstellar.setDuration(LocalTime.of(2, 49));
        interstellar.setPremiere(LocalDate.of(2014, 11, 7));
        iMovieRepository.save(interstellar);

        titanic = new Movie();
        titanic.setTitle("Titanic");
        titanic.setSynopsis("Titanic is a romantic drama directed by James Cameron, telling the story of Jack and " +
                "Rose.");
        titanic.setGenre(EMovieGenre.DRAMA);
        titanic.setAgeRating(EMovieAgeRating.EIGHTEEN_PLUS);
        titanic.setUserRating(EMovieUserRating.FIVE_STARS);
        titanic.setCoverImageUrl("https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg");
        titanic.setDuration(LocalTime.of(3, 15));
        titanic.setPremiere(LocalDate.of(1997, 12, 19));
        iMovieRepository.save(titanic);

        movieList.add(interstellar);
        movieList.add(titanic);
    }

    @Test
    void when_create_movie_then_returns_status_201() throws Exception {
        when(chatModel.call(anyString())).thenReturn("Interstellar is a science fiction film directed by " +
                "Christopher Nolan that explores themes of love.");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movie")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content("{\"title\":\"Interstellar\",\"synopsis\":\"Interstellar is a science fiction film directed" +
                        " by Christopher Nolan that explores themes of love.\",\"genre\":\"SCIENCE_FICTION\"," +
                        "\"ageRating\":\"SEVEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":\"https://pbs" +
                        ".twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg\",\"duration\":\"02:49\"," +
                        "\"premiere\":\"07-11-2014\"}\n"))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"title\":\"Interstellar\",\"synopsis\":\"Interstellar is a " +
                                "science fiction film directed by Christopher Nolan that explores themes of love." +
                                "\",\"genre\":\"SCIENCE_FICTION\",\"ageRating\":\"SEVEN_PLUS\",\"userRating\":" +
                                "\"FIVE_STARS\",\"coverImageUrl\":\"https://pbs.twimg.com/profile_images/55849" +
                                "0159834857472/gpoC7V0X_400x400.jpeg\",\"duration\":\"02:49\",\"premiere\":" +
                                "\"07-11-2014\"}\n"));
    }

    @Test
    void when_create_movie_with_null_title_then_returns_status_400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"title\":null,\"synopsis\":\"Interstellar is a science fiction film directed by" +
                                " Christopher Nolan that explores themes of love.\",\"genre\":\"SCIENCE_FICTION\"," +
                                "\"ageRating\":\"SEVEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":" +
                                "\"https://pbs.twimg.com/profile_images/558490159834857472/gpoC7V0X_400x400.jpeg\"," +
                                "\"duration\":\"02:49\",\"premiere\":\"07-11-2014\"}\n"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content().string("Title cannot be null\n"));
    }

    @Test
    void when_get_all_movies_then_returns_status_200_if_movies_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("[{\"title\":\"Interstellar\",\"synopsis\":\"Interstellar is a " +
                                "science fiction film directed by Christopher Nolan that explores themes of love." +
                                "\",\"genre\":\"SCIENCE_FICTION\",\"ageRating\":\"SEVEN_PLUS\",\"userRating\":" +
                                "\"FIVE_STARS\",\"coverImageUrl\":\"https://pbs.twimg.com/profile_images/55849015" +
                                "9834857472/gpoC7V0X_400x400.jpeg\",\"duration\":\"02:49\",\"premiere\":" +
                                "\"07-11-2014\"},{\"title\":\"Titanic\",\"synopsis\":\"Titanic is a romantic drama " +
                                "directed by James Cameron, telling the story of Jack and Rose.\",\"genre\":\"DRAMA\"" +
                                ",\"ageRating\":\"EIGHTEEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"coverImageUrl\":" +
                                "\"https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg\",\"duration\":" +
                                "\"03:15\",\"premiere\":\"19-12-1997\"}]\n"));
    }

    @Test
    void when_get_all_movies_should_return_status_200_and_empty_list_when_no_movies_exist() throws Exception {
        iMovieRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("[]"));
    }

    @Test
    void when_get_all_movies_titles_then_returns_status_200_if_movies_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movie/titles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("[Interstellar, Titanic]"));
    }

    @Test
    void when_get_all_movies_titles_should_return_status_200_and_empty_list_when_no_movies_exist() throws Exception {
        iMovieRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movie/titles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("[]"));
    }

    @Test
    void  when_get_movie_by_id_then_returns_status_200_if_movie_exists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movie/" + interstellar.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"title\":\"Interstellar\",\"synopsis\":\"Interstellar is a " +
                                "science fiction film directed by Christopher Nolan that explores themes of love.\"," +
                                "\"genre\":\"SCIENCE_FICTION\",\"ageRating\":\"SEVEN_PLUS\",\"userRating\":" +
                                "\"FIVE_STARS\",\"coverImageUrl\":\"https://pbs.twimg.com/profile_images/55849" +
                                "0159834857472/gpoC7V0X_400x400.jpeg\",\"duration\":\"02:49\",\"premiere\":\"" +
                                "07-11-2014\"}\n"));
    }

    @Test
    void  when_get_movie_by_id_then_returns_status_404_if_movie_not_found() throws Exception {
        iMovieRepository.deleteById(3L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movie/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Movie not found with ID: 3"));
    }

    @Test
    void  when_patch_movie_by_id_then_returns_status_200_if_update_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/movie/" + titanic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"title\":\"Inception\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"title\":\"Inception\",\"synopsis\":\"Titanic is a romantic " +
                                "drama directed by James Cameron, telling the story of Jack and Rose.\",\"genre\":" +
                                "\"DRAMA\",\"ageRating\":\"EIGHTEEN_PLUS\",\"userRating\":\"FIVE_STARS\",\"" +
                                "coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_" +
                                "poster.jpg\",\"duration\":\"03:15\",\"premiere\":\"19-12-1997\"}\n"));
    }

    @Test
    void  when_patch_movie_by_id_then_returns_status_404_if_movie_not_found() throws Exception {
        iMovieRepository.deleteById(3L);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/movie/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"title\":\"Inception\"}"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Movie not found with ID: 3"));
    }

    @Test
    void  when_delete_movie_by_id_then_returns_status_200_if_deletion_is_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movie/" + interstellar.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().string("Movie was successfully deleted"));
    }

    @Test
    void when_delete_movie_by_id_then_returns_status_404_if_movie_not_found() throws Exception {
        iMovieRepository.deleteById(3L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movie/3")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Movie not found with ID: 3"));
    }

    @Test
    void when_delete_movies_by_ids_then_returns_status_200_if_deletion_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movie")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(String.format("[%d, %d]", interstellar.getId(), titanic.getId())))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                    .content().string("Movies were successfully deleted"));
    }

    @Test
    void when_delete_movies_by_ids_then_returns_status_404_if_movies_not_found() throws Exception {
        iMovieRepository.deleteById(titanic.getId());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(String.format("[%d, %d]", interstellar.getId(), titanic.getId())))
                        .andExpect(status().isNotFound())
                        .andExpect(MockMvcResultMatchers
                            .content().string("Some movies not found"));
    }

    @Test
    void when_delete_movies_by_ids_then_returns_status_400_if_deletion_fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content().string("Movie IDs cannot be null or empty"));
    }
}
