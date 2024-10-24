package es.fjrj3d.seat_booker_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class WebSocketService {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void sendDurationUpdate(Long screeningId, Duration remainingDuration) {
        messagingTemplate.convertAndSend("/topic/screening/" + screeningId, remainingDuration.toString());
    }

    public void sendScreeningEnded(Long screeningId) {
        messagingTemplate.convertAndSend("/topic/screening/" + screeningId, "The screening has ended");
    }
}
