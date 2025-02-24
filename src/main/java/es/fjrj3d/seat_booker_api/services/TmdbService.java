package es.fjrj3d.seat_booker_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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
}
