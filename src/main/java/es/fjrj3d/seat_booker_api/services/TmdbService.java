package es.fjrj3d.seat_booker_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Autowired
    RestTemplate restTemplate;

    public Map<String, Object> getNowPlayingMovies() {
        String startDate = LocalDate.now().minusMonths(3).toString();
        String endDate = LocalDate.now().toString();

        String url = "https://api.themoviedb.org/3/discover/movie?language=es-ES&page=1&primary_release_date.gte="
                + startDate + "&primary_release_date.lte=" + endDate
                + "&sort_by=popularity.desc&api_key=" + apiKey;

        return restTemplate.getForObject(url, Map.class);
    }

    public List<String> getGenreNamesByIds(List<Integer> genreIds) {
        Map<Integer, String> genreMap = new HashMap<>();

        genreMap.put(28, "Acción");
        genreMap.put(12, "Aventura");
        genreMap.put(16, "Animación");
        genreMap.put(35, "Comedia");
        genreMap.put(80, "Crimen");
        genreMap.put(99, "Documental");
        genreMap.put(18, "Drama");
        genreMap.put(10751, "Familiar");
        genreMap.put(14, "Fantasía");
        genreMap.put(36, "Historia");
        genreMap.put(27, "Terror");
        genreMap.put(10402, "Música");
        genreMap.put(9648, "Misterio");
        genreMap.put(10749, "Romance");
        genreMap.put(878, "Ciencia Ficción");
        genreMap.put(10770, "Película de TV");
        genreMap.put(53, "Suspense");
        genreMap.put(10752, "Guerra");
        genreMap.put(37, "Western");

        return genreIds.stream()
                .map(id -> genreMap.getOrDefault(id, "Género desconocido"))
                .collect(Collectors.toList());
    }

    public LocalDate convertToLocalDate(String releaseDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(releaseDate, formatter);
    }

    public Integer getMovieDuration(Integer movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        return response != null ? (Integer) response.get("runtime") : null;
    }

    public LocalTime convertIntegerToLocalTime(Integer minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return LocalTime.of(hours, remainingMinutes);
    }
}
