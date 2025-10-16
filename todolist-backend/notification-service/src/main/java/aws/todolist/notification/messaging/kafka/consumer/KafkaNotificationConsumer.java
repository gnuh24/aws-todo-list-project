package aws.todolist.notification.messaging.kafka.consumer;

import aws.todolist.notification.entity.Notification.NotificationType;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationConsumer {
	
	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;
	
	// ===========================================================
	// 🧩 PROJECT EVENTS
	// ===========================================================
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.project-member-added}", groupId = "notification-service")
	public void consumeProjectMemberAdded(String messageJson) {
		processMessage(messageJson, NotificationType.PROJECT_MEMBER_ADDED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.project-member-role-updated}", groupId = "notification-service")
	public void consumeProjectMemberRoleUpdated(String messageJson) {
		processMessage(messageJson, NotificationType.PROJECT_MEMBER_ROLE_UPDATED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.project-deleted}", groupId = "notification-service")
	public void consumeProjectDeleted(String messageJson) {
		processMessage(messageJson, NotificationType.PROJECT_DELETED);
	}
	
	// ===========================================================
	// 🧩 TASK EVENTS
	// ===========================================================
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-assigned}", groupId = "notification-service")
	public void consumeTaskAssigned(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_ASSIGNED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-commented}", groupId = "notification-service")
	public void consumeTaskCommented(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_COMMENTED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-updated}", groupId = "notification-service")
	public void consumeTaskUpdated(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_UPDATED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-completed}", groupId = "notification-service")
	public void consumeTaskCompleted(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_COMPLETED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-reopened}", groupId = "notification-service")
	public void consumeTaskReopened(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_REOPENED);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-due-soon}", groupId = "notification-service")
	public void consumeTaskDueSoon(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_DUE_SOON);
	}
	
	@KafkaListener(topics = "${app.kafka.topic.taskflow.notification.task-overdue}", groupId = "notification-service")
	public void consumeTaskOverdue(String messageJson) {
		processMessage(messageJson, NotificationType.TASK_OVERDUE);
	}
	
	// ===========================================================
	// ⚙️ COMMON HANDLER
	// ===========================================================
	
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
