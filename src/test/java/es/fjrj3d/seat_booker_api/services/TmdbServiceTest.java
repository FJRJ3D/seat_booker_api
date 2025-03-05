package es.fjrj3d.seat_booker_api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TmdbServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TmdbService tmdbService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tmdbService, "apiKey", "fake-api-key");
    }

    @Test
    public void testGetNowPlayingMovies() {
        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("result", "ok");

        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(fakeResponse);

        Map<String, Object> result = tmdbService.getNowPlayingMovies();
        assertEquals("ok", result.get("result"));
    }

    @Test
    public void testGetGenreNamesByIds() {
        List<Integer> ids = Arrays.asList(28, 12, 999);
        List<String> expected = Arrays.asList("Acción", "Aventura", "Género desconocido");
        List<String> actual = tmdbService.getGenreNamesByIds(ids);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertToLocalDate() {
        String dateStr = "2023-03-01";
        LocalDate expected = LocalDate.of(2023, 3, 1);
        LocalDate actual = tmdbService.convertToLocalDate(dateStr);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetMovieDuration() {
        Integer movieId = 123;
        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("runtime", 120);

        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(fakeResponse);

        Integer duration = tmdbService.getMovieDuration(movieId);
        assertEquals(120, duration);
    }

    @Test
    public void testConvertIntegerToLocalTime() {
        int minutes = 125;
        LocalTime expected = LocalTime.of(2, 5);
        LocalTime actual = tmdbService.convertIntegerToLocalTime(minutes);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetMovieAgeRatingByIdFound() {
        Long movieId = 456L;

        Map<String, Object> releaseDateEntry = new HashMap<>();
        releaseDateEntry.put("certification", "18");

        List<Map<String, Object>> releaseDates = new ArrayList<>();
        releaseDates.add(releaseDateEntry);

        Map<String, Object> countryData = new HashMap<>();
        countryData.put("iso_3166_1", "ES");
        countryData.put("release_dates", releaseDates);

        List<Map<String, Object>> results = new ArrayList<>();
        results.add(countryData);

        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("results", results);

        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(fakeResponse);

        String certification = tmdbService.getMovieAgeRatingById(movieId);
        assertEquals("18", certification);
    }

    @Test
    public void testGetMovieAgeRatingByIdNotFound() {
        Long movieId = 456L;
        Map<String, Object> countryData = new HashMap<>();
        countryData.put("iso_3166_1", "US");
        countryData.put("release_dates", Collections.emptyList());

        List<Map<String, Object>> results = new ArrayList<>();
        results.add(countryData);

        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("results", results);

        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(fakeResponse);

        String certification = tmdbService.getMovieAgeRatingById(movieId);
        assertEquals("Sin clasificación", certification);
    }

    @Test
    public void testGetMovieAgeRatingByIdNullResponse() {
        Long movieId = 456L;
        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(null);
        String certification = tmdbService.getMovieAgeRatingById(movieId);
        assertEquals("Sin clasificación", certification);
    }
}