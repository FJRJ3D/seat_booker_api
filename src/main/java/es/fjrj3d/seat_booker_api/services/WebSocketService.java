package es.fjrj3d.seat_booker_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void sendDurationUpdate(Long screeningId, String message) {
        messagingTemplate.convertAndSend("/topic/screening/" + screeningId, message);
    }

    public void sendScreeningEnded(Long screeningId) {
        messagingTemplate.convertAndSend("/topic/screening/" + screeningId, "The screening has ended");
    }
}
