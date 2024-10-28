package es.fjrj3d.seat_booker_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeatBookerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatBookerApiApplication.class, args);
	}

}
