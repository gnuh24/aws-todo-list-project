package aws.todolist.notification.messaging.kafka.producer;

import aws.todolist.notification.dto.eventNotification.NotificationEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.notification-events}")
    private String notificationTopic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendNotificationEvent(String key, NotificationEventPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(notificationTopic, key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize notification payload to JSON", e);
        }
    }
}
