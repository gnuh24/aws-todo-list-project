package aws.todolist.taskflow.messaging.kafka.producer;

import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
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
	
	// ======= Topic Config =======
	@Value("${app.kafka.topic.taskflow.notification.project-member-added}")
	private String projectMemberAddedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.project-member-role-updated}")
	private String projectMemberRoleUpdatedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-assigned}")
	private String taskAssignedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-commented}")
	private String taskCommentedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-updated}")
	private String taskUpdatedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-completed}")
	private String taskCompletedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-reopened}")
	private String taskReopenedTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-due-soon}")
	private String taskDueSoonTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.task-overdue}")
	private String taskOverdueTopic;
	
	@Value("${app.kafka.topic.taskflow.notification.project-deleted}")
	private String projectDeletedTopic;
	
	// ======= SENDERS =======
	
	public void sendProjectMemberAdded(NotificationMessage message) {
		sendMessage(projectMemberAddedTopic, message);
	}
	
	public void sendProjectMemberRoleUpdated(NotificationMessage message) {
		sendMessage(projectMemberRoleUpdatedTopic, message);
	}
	
	public void sendTaskAssigned(NotificationMessage message) {
		sendMessage(taskAssignedTopic, message);
	}
	
	public void sendTaskCommented(NotificationMessage message) {
		sendMessage(taskCommentedTopic, message);
	}
	
	public void sendTaskUpdated(NotificationMessage message) {
		sendMessage(taskUpdatedTopic, message);
	}
	
	public void sendTaskCompleted(NotificationMessage message) {
		sendMessage(taskCompletedTopic, message);
	}
	
	public void sendTaskReopened(NotificationMessage message) {
		sendMessage(taskReopenedTopic, message);
	}
	
	public void sendTaskDueSoon(NotificationMessage message) {
		sendMessage(taskDueSoonTopic, message);
	}
	
	public void sendTaskOverdue(NotificationMessage message) {
		sendMessage(taskOverdueTopic, message);
	}
	
	public void sendProjectDeleted(NotificationMessage message) {
		sendMessage(projectDeletedTopic, message);
	}
	
	// ======= COMMON HANDLER =======
	private void sendMessage(String topic, NotificationMessage message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			kafkaTemplate.send(topic, json);
			System.out.printf("📤 Sent notification [%s] to topic [%s]%n", message.getType(), topic);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("❌ Lỗi khi serialize NotificationMessage", e);
		}
	}
}
