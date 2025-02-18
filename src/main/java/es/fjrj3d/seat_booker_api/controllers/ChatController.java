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

//    @GetMapping("/ai/generate")
//    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Cuentame un chiste en " +
//            "español, pero no añadas nada mas, solo el chiste") String message) {
//        return Map.of("generation", this.chatModel.call(message));
//    }

//    @GetMapping("/ai/generate")
//    public Map<String,String> generate(@RequestParam(value = "message",
//            defaultValue = "Cuentame un chiste en español, pero no añadas nada mas, solo el chiste") String message) {
//
//        String output = this.chatModel.call(message);
//        String cleanedOutput = output.replaceAll("(?s)<think>.*?</think>", "").trim();
//        cleanedOutput = cleanedOutput.replaceAll("[\\n\\r\\t]+", " ").trim();
//        cleanedOutput = cleanedOutput.replace("\\\"", "\"");
//        cleanedOutput = cleanedOutput.replaceAll("\\s{2,}", " ");
//
//        return Map.of("generation", cleanedOutput);
//    }

//    @GetMapping("/ai/generate")
//    public Map<String,String> generate(@RequestParam(value = "message",
//            defaultValue = "Cuentame un chiste en español, pero no añadas nada mas, solo el chiste") String message) {
//
//        message = "Quiero que actues como un recomendador de peliculas. Estas son las peliculas en la cartelera del cine: Alien, Titanic, La vida es bella, Solaris y Avatar. Estoy buscando alguna que sea similar a estas que me gustaron: Terminator, El dia de mañana, Ameli, " + movieService.getMovieById(2L).getTitle()+ ". Es importante que no menciones las peliculas que no sean similares en ningun momento, y desarrollame las que si son similares y porque me las recomiendas. Contestame en Español, por favor";
//
//        String output = this.chatModel.call(message);
//        String cleanedOutput = output.replaceAll("(?s)<think>.*?</think>", "").trim();
//        cleanedOutput = cleanedOutput.replaceAll("[\\n\\r\\t]+", " ").trim();
//        cleanedOutput = cleanedOutput.replace("\\\"", "\"");
//        cleanedOutput = cleanedOutput.replaceAll("\\s{2,}", " ");
//
//        return Map.of("generation", cleanedOutput);
//    }

    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Cuentame un chiste en " +
            "español, pero no añadas nada mas, solo el chiste") String message) {
        message = "Quiero que actues como un recomendador de peliculas. Estas son las peliculas en la cartelera del cine: Alien, Titanic, La vida es bella, Solaris y Avatar y estoy buscando alguna que sea similar a estas que me gustaron: Terminator, El dia de mañana, Ameli, " + movieService.getMovieById(2L).getTitle()+ ". Contestame en Español, por favor";
        return Map.of("generation", this.chatModel.call(message));
    }

//    @GetMapping("/ai/generateStream")
//    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Cuentame un chiste") String message) {
//        Prompt prompt = new Prompt(new UserMessage(message));
//        return this.chatModel.stream(prompt);
//    }

    List<String> history = new ArrayList<>();

//    @GetMapping("/ai/generateStream")
//    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Cuentame un chiste") String message) {
//
//        Prompt prompt = new Prompt(new UserMessage("[Contexto Conversacion]/nFran: eres Chanchito Feliz mi asistente personal de este cine que tiene en la cartelera las siguientes peliculas: Terminator, Ameli y El dia de Mañana./nAnteriormente he visto y me han gustado: Avatar, Interstellar y Solaris./n[Historial Conversación]/n" + history + "/n" + "[Pregunta]/n" + message));
//
//        String qwen = "Tu: " + this.chatModel.call(prompt).toString() + "/n";
//        history.add("Yo: " + message + "/n");
//        history.add(qwen);
//
//        return this.chatModel.stream(prompt);
//    }

    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Cuentame un chiste") String message) {
        Prompt prompt = new Prompt(new UserMessage("[Contexto Conversacion]\nSoy Fran y ya me conoces y tu eres Chanchito Feliz mi asistente personal de este cine que tiene en la cartelera las siguientes peliculas: Terminator, Ameli y El dia de Mañana.\nAnteriormente he visto y me han gustado: Avatar, Interstellar y Solaris.\n[Historial Conversación]\n" + history + "\n" + "[Pregunta]\n" + message));

        String qwen = "Tu: " + this.chatModel.call(prompt).toString() + "\n";
        history.add("Yo: " + message + "\n");
        history.add(qwen);

        return this.chatModel.stream(prompt);
    }
}
