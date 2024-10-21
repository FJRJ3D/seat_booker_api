package es.fjrj3d.seat_booker_api.exceptions;

public class MovieNotFoundException extends RuntimeException{

    public MovieNotFoundException(String message){
        super(message);
    }
}
