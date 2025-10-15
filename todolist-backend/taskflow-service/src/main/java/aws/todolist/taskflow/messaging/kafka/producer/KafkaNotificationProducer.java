package aws.todolist.taskflow.messaging.kafka.producer;

import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===== Topic config =====
    @Value("${app.kafka.topic.taskflow.notification.project-member-added}")
    private String projectMemberAddedTopic;

    @Value("${app.kafka.topic.taskflow.notification.project-member-role-updated}")
    private String projectMemberRoleUpdatedTopic;

    /**
     * Gửi thông báo khi có thành viên mới được thêm vào project
     */
    public void sendProjectMemberAdded(NotificationMessage message) {
        sendMessage(projectMemberAddedTopic, message);
    }

    /**
     * Gửi thông báo khi vai trò của thành viên trong project được cập nhật
     */
    public void sendProjectMemberRoleUpdated(NotificationMessage message) {
        sendMessage(projectMemberRoleUpdatedTopic, message);
    }

    /**
     * Hàm xử lý gửi message chung
     */
    private void sendMessage(String topic, NotificationMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi serialize NotificationMessage", e);
        }
    }
}
