package aws.todoist.websocket.messaging.kafka.consumer;

import aws.todoist.websocket.dto.taskflow.EventEnvelope;
import aws.todoist.websocket.dto.taskflow.payload.*;
import aws.todoist.websocket.enums.eventDto.EventType;
import aws.todoist.websocket.service.WebSocketService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProjectConsumer {

    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

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

        ProjectPayload payload = event.getPayload();
        String projectId = payload.getProjectId();

        // 1️⃣ realtime project detail
        webSocketService.sendProjectEvent(projectId, event);

        // 2️⃣ update project summary (sidebar)
        if (payload.getReceivers() != null) {
            for (String email : payload.getReceivers()) {
                webSocketService.sendProjectSummaryToUser(
                        email,
                        event   // hoặc DTO gọn hơn
                );
            }
        }
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

        webSocketService.sendTaskEvent(
                event.getPayload().getProjectId(),
                event
        );
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

        webSocketService.sendSectionEvent(
                event.getPayload().getProjectId(),
                event
        );
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

        MemberPayload payload = event.getPayload();
        String projectId = payload.getProjectId();

        // 1️⃣ realtime member detail
        webSocketService.sendMemberEvent(projectId, event);

        // 2. kiểm tra xem event có phải là accept ko thì update giao diện sidebar
        if(event.getEventType() == EventType.PROJECT_MEMBER_ACCEPTED || event.getEventType() == EventType.PROJECT_MEMBER_REMOVED){
            webSocketService.sendProjectSummaryToUser(event.getPayload().getMemberEventDto().getEmail(), event);
        }
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

        System.err.println(event);

        webSocketService.sendCommentEvent(
                event.getPayload().getProjectId(),
                event
        );
    }
}
