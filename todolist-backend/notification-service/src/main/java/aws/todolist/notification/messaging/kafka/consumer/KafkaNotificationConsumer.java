package aws.todolist.notification.messaging.kafka.consumer;

import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.entity.Notification.NotificationType;
import aws.todolist.notification.repository.NotificationRepository;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    /**
     * Lắng nghe khi có thành viên mới được thêm vào project
     */
    @KafkaListener(topics = "${app.kafka.topic.taskflow.notification.project-member-added}", groupId = "notification-service")
    public void consumeProjectMemberAdded(String messageJson) {
        processMessage(messageJson, NotificationType.PROJECT_MEMBER_ADDED);
    }

    /**
     * Lắng nghe khi vai trò của thành viên được cập nhật
     */
    @KafkaListener(topics = "${app.kafka.topic.taskflow.notification.project-member-role-updated}", groupId = "notification-service")
    public void consumeProjectMemberRoleUpdated(String messageJson) {
        processMessage(messageJson, NotificationType.PROJECT_MEMBER_ROLE_UPDATED);
    }

    /**
     * Xử lý message chung
     */
    private void processMessage(String messageJson, NotificationType expectedType) {
        try {
            NotificationMessage msg = objectMapper.readValue(messageJson, NotificationMessage.class);

            if (msg.getType() != expectedType) {
                log.warn("⚠️  Bỏ qua message không đúng type (expected={}, got={})", expectedType, msg.getType());
                return;
            }

	    notificationService.create(msg);
            log.info("✅ [Kafka] Saved notification: {} for receiver {}", msg.getType(), msg.getReceiverId());

        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý NotificationMessage: {}", e.getMessage(), e);
        }
    }
}
