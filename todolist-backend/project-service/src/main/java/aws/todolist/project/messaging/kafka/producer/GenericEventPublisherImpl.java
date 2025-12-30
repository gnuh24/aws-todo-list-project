package aws.todolist.project.messaging.kafka.producer;

import aws.todolist.project.dto.event.EventEnvelope;
import aws.todolist.project.dto.event.payload.*;
import aws.todolist.project.enums.EventType;
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


    @Value("${app.kafka.topic.project-events}")    // project.project-events.v1
    private String projectEventsTopic;

    @Value("${app.kafka.topic.section-events}")    // project.section-events.v1
    private String sectionEventsTopic;

    @Value("${app.kafka.topic.member-events}")     // project.member-events.v1
    private String memberEventsTopic;

    @Override
    @Transactional
    public void publishProjectEvent(String projectId, ProjectPayload payload, EventType eventType) {
        String eventJson = createEvent(payload, eventType);
        kafkaTemplate.send(projectEventsTopic, projectId, eventJson);
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