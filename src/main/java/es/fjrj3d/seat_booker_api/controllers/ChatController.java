package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.services.MovieService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    MovieService movieService;

    private final OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    List<String> history = new ArrayList<>();

    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Cuentame un chiste") String message) {
        Prompt prompt = new Prompt(new UserMessage("[Contexto Conversacion]\n\nSoy Fran y ya me conoces y tu eres Chanchito Feliz mi asistente personal de este cine que tiene en la cartelera las siguientes peliculas: " +movieService.getAllMoviesTitles()+ ".\n\nAnteriormente he visto y me han gustado: Avatar, Interstellar y Solaris.\n\n[Instrucciones]\n\nQuiero que si te digo que borres todas las peliculas me contestes: Borrando.\n\nQuiero que si te digo que crees todas las peliculas me contestes: Creando.\n\n[Historial Conversación]\n\n" + history + "\n\n" + "[Pregunta]\n\n" + message));

        String qwen = "Tu: " + this.chatModel.call(prompt).toString() + "\n";
        history.add("Yo: " + message + "\n");
        history.add(qwen);

        if (this.chatModel.call(prompt.getContents().toString()).equals("Borrando.")){
            movieService.deleteAllMovies();
        } else if (this.chatModel.call(prompt.getContents().toString()).equals("Creando.")) {
            movieService.createMovieList();
        }

        return this.chatModel.stream(prompt);
    }
}
