package es.fjrj3d.seat_booker_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude = {SecurityAutoConfiguration.class})
public class SeatBookerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatBookerApiApplication.class, args);
	}

}
