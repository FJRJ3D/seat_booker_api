package es.fjrj3d.seat_booker_api.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Duration;

public class ScreeningWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    public void sendRemainingTime(WebSocketSession session, Long screeningId, Duration duration) {
        Long remainingTime = duration.getSeconds();
        try {
            session.sendMessage(new TextMessage("Screening ID: " + screeningId + " - Remaining Time: " + remainingTime + " seconds"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
