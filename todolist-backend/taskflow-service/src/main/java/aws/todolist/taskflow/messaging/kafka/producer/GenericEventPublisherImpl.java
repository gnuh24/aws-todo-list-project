package aws.todolist.taskflow.messaging.kafka.producer;

import aws.todolist.taskflow.dto.event.EventEnvelope;
import aws.todolist.taskflow.dto.event.payload.*;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.mapper.ProjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericEventPublisherImpl implements GenericEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    private ProjectMapper projectMapper;


    @Value("${app.kafka.topic.project-events}")    // taskflow.project-events.v1
    private String projectEventsTopic;

    @Value("${app.kafka.topic.task-events}")       // taskflow.task-events.v1
    private String taskEventsTopic;

    @Value("${app.kafka.topic.comment-events}")    // taskflow.comment-events.v1
    private String commentEventsTopic;

    @Value("${app.kafka.topic.section-events}")    // taskflow.section-events.v1
    private String sectionEventsTopic;

    @Value("${app.kafka.topic.member-events}")     // taskflow.member-events.v1
    private String memberEventsTopic;

    @Override
    @Transactional
    public void publishProjectEvent(String projectId, ProjectPayload payload, EventType eventType) {
        String eventJson = createEvent(payload, eventType);
        kafkaTemplate.send(projectEventsTopic, projectId, eventJson);
    }

    @Override
    @Transactional
    public void publishTaskEvent(String projectId, TaskPayload payload, EventType eventType) {
        String eventJson = createEvent(payload, eventType);
        kafkaTemplate.send(taskEventsTopic, projectId, eventJson);
    }

    @Override
    @Transactional
    public void publishCommentEvent(String projectId, CommentPayload payload, EventType eventType) {
        String eventJson = createEvent(payload, eventType);
        kafkaTemplate.send(commentEventsTopic, projectId, eventJson);
        log.info("Published {} for comment {}", eventType, projectId);
    }

    @Override
    @Transactional
    public void publishSectionEvent(String projectId, SectionPayload payload, EventType eventType) {
        String eventJson = createEvent(payload, eventType);
        kafkaTemplate.send(sectionEventsTopic, projectId, eventJson);
        log.info("Published {} for section {}", eventType, projectId);
    }

    @Override
    @Transactional
    public void publishMemberEvent(String projectId, MemberPayload payload, EventType eventType) {
        String eventJson = createEvent(payload, eventType);
        kafkaTemplate.send(memberEventsTopic, projectId, eventJson);
        log.info("Published {} for member {}", eventType, projectId);
    }

    private <T> String createEvent(T payload, EventType eventType) {
        try {
            EventEnvelope<T> envelope = EventEnvelope.<T>builder()
                    .eventType(eventType)
                    .version("v1")
                    .occurredAt(Instant.now())
                    .payload(payload)
                    .build();

            return objectMapper.writeValueAsString(envelope);

        } catch (Exception e) {
            log.error("❌ Cannot serialize event", e);
            throw new RuntimeException("Cannot serialize event");
        }
    }

}