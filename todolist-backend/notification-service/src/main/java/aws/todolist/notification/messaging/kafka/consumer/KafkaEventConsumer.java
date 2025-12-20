package aws.todolist.notification.messaging.kafka.consumer;

import aws.todolist.notification.dto.event.EventEnvelope;
import aws.todolist.notification.dto.event.payload.*;
import aws.todolist.notification.messaging.kafka.dispatcher.EventDispatcher;
import aws.todolist.notification.service.NotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;

	@Autowired
	private EventDispatcher eventDispatcher;

	// ================= PROJECT =================
	@KafkaListener(
			topics = "${app.kafka.topic.project-events}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	public void consumeProjectEvent(String message) throws Exception {

		EventEnvelope<ProjectPayload> event =
				objectMapper.readValue(
						message,
						new TypeReference<EventEnvelope<ProjectPayload>>() {
						}
				);

		eventDispatcher.dispatch(event);

	}

	// ================= TASK =================
	@KafkaListener(topics = "${app.kafka.topic.task-events}",
			groupId = "${spring.kafka.consumer.group-id}")
	public void consumeTaskEvent(String message) throws Exception {

		EventEnvelope<TaskPayload> event =
				objectMapper.readValue(
						message,
						new TypeReference<EventEnvelope<TaskPayload>>() {
						}
				);

		eventDispatcher.dispatch(event);

	}

	// ================= SECTION =================
	@KafkaListener(topics = "${app.kafka.topic.section-events}",
			groupId = "${spring.kafka.consumer.group-id}")
	public void consumeSectionEvent(String message) throws Exception {

		EventEnvelope<SectionPayload> event =
				objectMapper.readValue(
						message,
						new TypeReference<EventEnvelope<SectionPayload>>() {
						}
				);

		eventDispatcher.dispatch(event);

	}

	// ================= MEMBER =================
	@KafkaListener(topics = "${app.kafka.topic.member-events}",
			groupId = "${spring.kafka.consumer.group-id}")
	public void consumeMemberEvent(String message) throws Exception {

		EventEnvelope<MemberPayload> event =
				objectMapper.readValue(
						message,
						new TypeReference<EventEnvelope<MemberPayload>>() {
						}
				);

		eventDispatcher.dispatch(event);


	}

	// ================= COMMENT =================
	@KafkaListener(topics = "${app.kafka.topic.comment-events}",
			groupId = "${spring.kafka.consumer.group-id}")
	public void consumeCommentEvent(String message) throws Exception {

		EventEnvelope<CommentPayload> event =
				objectMapper.readValue(
						message,
						new TypeReference<EventEnvelope<CommentPayload>>() {
						}
				);

		eventDispatcher.dispatch(event);

	}
}
