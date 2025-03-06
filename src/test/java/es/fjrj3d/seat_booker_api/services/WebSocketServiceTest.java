package es.fjrj3d.seat_booker_api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class WebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketService webSocketService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_send_duration_update() {
        Long screeningId = 1L;
        String message = "Duration updated";

        webSocketService.sendDurationUpdate(screeningId, message);

        verify(messagingTemplate, times(1)).convertAndSend("/topic/screening/" + screeningId, message);
    }

    @Test
    public void test_send_screening_ended() {
        Long screeningId = 1L;

        webSocketService.sendScreeningEnded(screeningId);

        verify(messagingTemplate, times(1)).convertAndSend("/topic/screening/" + screeningId, "The screening has ended");
    }
}