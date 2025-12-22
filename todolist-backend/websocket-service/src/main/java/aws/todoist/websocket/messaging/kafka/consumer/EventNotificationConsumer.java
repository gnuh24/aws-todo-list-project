package aws.todoist.websocket.messaging.kafka.consumer;

import aws.todoist.websocket.dto.notification.NotificationEventPayload;
import aws.todoist.websocket.service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationConsumer {

    private final ObjectMapper objectMapper;

    @Autowired
    private WebSocketService webSocketService;

    public EventNotificationConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.notification-events}",
            groupId = "notification-service"
    )
    public void consume(String message) {
        try {
            NotificationEventPayload payload =
                    objectMapper.readValue(message, NotificationEventPayload.class);

            webSocketService.sendNotificationToUser(payload.getEmail(), payload.getNotification());

        } catch (Exception e) {
            throw new RuntimeException("Failed to consume notification event", e);
        }
    }
}