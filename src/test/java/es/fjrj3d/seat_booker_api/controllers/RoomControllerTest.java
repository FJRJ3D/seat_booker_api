package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.dtos.request.RegisterRequest;
import es.fjrj3d.seat_booker_api.models.*;
import es.fjrj3d.seat_booker_api.repositories.IMovieRepository;
import es.fjrj3d.seat_booker_api.repositories.IRoomRepository;
import es.fjrj3d.seat_booker_api.services.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IRoomRepository iRoomRepository;

    @Autowired
    private IMovieRepository iMovieRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;
    private Room room1;
    private Room room2;
    private Movie interstellar;
    private RegisterRequest registerRequest;

    private final List<Room> roomList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE movie AUTO_INCREMENT = 1;");
        jdbcTemplate.execute("ALTER TABLE room AUTO_INCREMENT = 1;");
        jdbcTemplate.execute("ALTER TABLE user AUTO_INCREMENT = 1;");

        registerRequest = new RegisterRequest("user@gmail.com", "user", "user");
        token = authService.register(registerRequest).accessToken();

        iRoomRepository.deleteAll();
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

        room1 = new Room();
        room1.setRoomName("Room 1");
        room1.setRoomType(ERoomType.STANDARD);
        room1.setRowQuantity(8);
        room1.setSeatQuantity(9);
        room1.setMovie(interstellar);
        iRoomRepository.save(room1);

        room2 = new Room();
        room2.setRoomName("Room 2");
        room2.setRoomType(ERoomType.IMAX);
        room2.setRowQuantity(10);
        room2.setSeatQuantity(12);
        room2.setMovie(interstellar);
        iRoomRepository.save(room2);

        roomList.add(room1);
        roomList.add(room2);
    }

    @Test
    void when_create_room_then_returns_status_201() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/room/" + interstellar.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"roomType\":\"STANDARD\",\"rowQuantity\":8,\"seatQuantity\":9}\n"))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"roomType\":\"STANDARD\",\"rowQuantity\":8,\"seatQuantity\":" +
                                "9}\n"));
    }

    @Test
    void when_create_and_delete_room_then_roomName_reuses_deleted_position() throws Exception {
        iRoomRepository.deleteAll();

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/room/" + interstellar.getTitle())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content("{\"roomType\":\"STANDARD\",\"rowQuantity\":8,\"seatQuantity\":9}\n"))
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers
                            .content().json("{\"roomType\":\"STANDARD\",\"rowQuantity\":8,\"seatQuantity" +
                                    "\":9}\n"));
        }

        iRoomRepository.deleteById(iRoomRepository.findByRoomName("Room 3").get().getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/room/" + interstellar.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"roomType\":\"STANDARD\",\"rowQuantity\":8,\"seatQuantity\":9}\n"))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"roomType\":\"STANDARD\",\"rowQuantity\":8,\"seatQuantity\":" +
                                "9}\n"));

        assertEquals("Room 1", iRoomRepository.findByRoomName("Room 1").get().getRoomName());
        assertEquals("Room 2", iRoomRepository.findByRoomName("Room 2").get().getRoomName());
        assertEquals("Room 3", iRoomRepository.findByRoomName("Room 3").get().getRoomName());
        assertEquals("Room 4", iRoomRepository.findByRoomName("Room 4").get().getRoomName());
        assertEquals("Room 5", iRoomRepository.findByRoomName("Room 5").get().getRoomName());
    }

    @Test
    void when_create_room_with_null_rowQuantity_then_returns_status_400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/room/" + interstellar.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"roomType\":\"STANDARD\",\"rowQuantity\":null,\"seatQuantity\":9}\n"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content().string("Row Quantity cannot be null\n"));
    }

    @Test
    void when_get_all_rooms_then_returns_status_200_if_rooms_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("[{\"id\":1,\"roomName\":\"Room 1\",\"roomType\":\"STANDARD\"," +
                                "\"rowQuantity\":8,\"seatQuantity\":9,\"screenings\":null},{\"id\":2,\"roomName\":" +
                                "\"Room 2\",\"roomType\":\"IMAX\",\"rowQuantity\":10,\"seatQuantity\":12,\"" +
                                "screenings\":null}]\n"));
    }

    @Test
    void  when_get_room_by_id_then_returns_status_200_if_room_exists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/room/" + room1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"id\":1,\"roomName\":\"Room 1\",\"roomType\":\"STANDARD\"," +
                                "\"rowQuantity\":8,\"seatQuantity\":9,\"screenings\":null}"));
    }

    @Test
    void  when_get_room_by_id_then_returns_status_404_if_room_not_found() throws Exception {
        iRoomRepository.deleteById(3L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/room/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Room not found with id: 3"));
    }

    @Test
    void when_get_movie_title_then_returns_rooms_assigned_to_the_movie_status_200_if_movie_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/room/movie/" + interstellar.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("[{\"id\":1,\"roomName\":\"Room 1\",\"roomType\":\"STANDARD\"," +
                                "\"rowQuantity\":8,\"seatQuantity\":9,\"screenings\":null},{\"id\":2,\"roomName\":" +
                                "\"Room 2\",\"roomType\":\"IMAX\",\"rowQuantity\":10,\"seatQuantity\":12,\"" +
                                "screenings\":null}]\n"));
    }

    @Test
    void when_get_movie_title_then_returns_status_400_if_movie_does_not_exists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/room/movie/Titanic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Movie not found with title: Titanic"));
    }

    @Test
    void  when_patch_room_by_id_then_returns_status_200_if_update_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/room/" + room2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"rowQuantity\": 5}\n"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json("{\"roomName\":\"Room 2\",\"roomType\":\"IMAX\",\"rowQuantity\":5," +
                                "\"seatQuantity\":12}\n"));
    }

    @Test
    void  when_patch_room_by_id_then_returns_status_404_if_room_not_found() throws Exception {
        iRoomRepository.deleteById(3L);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/room/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("{\"rowQuantity\": 5}\n"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Room not found with ID: 3"));
    }

    @Test
    void  when_delete_room_by_id_then_returns_status_200_if_deletion_is_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/room/" + room1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().string("Room was successfully deleted"));
    }

    @Test
    void when_delete_room_by_id_then_returns_status_404_if_room_not_found() throws Exception {
        iRoomRepository.deleteById(3L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/room/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Room not found with id: 3"));
    }

    @Test
    void when_delete_rooms_by_ids_then_returns_status_200_if_deletion_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(String.format("[%d, %d]", room1.getId(), room2.getId())))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().string("Rooms were successfully deleted"));
    }

    @Test
    void when_delete_rooms_by_ids_then_returns_status_404_if_rooms_not_found() throws Exception {
        iRoomRepository.deleteById(room2.getId());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(String.format("[%d, %d]", room1.getId(), room2.getId())))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content().string("Some rooms not found"));
    }

    @Test
    void when_delete_rooms_by_ids_then_returns_status_400_if_deletion_fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content().string("Room IDs cannot be null or empty"));
    }
}
