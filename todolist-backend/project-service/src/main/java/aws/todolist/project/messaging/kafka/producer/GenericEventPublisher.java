package aws.todolist.project.messaging.kafka.producer;

import aws.todolist.project.dto.event.payload.*;
import aws.todolist.project.enums.EventType;
import org.springframework.stereotype.Service;

@Service
public interface GenericEventPublisher {
    void publishProjectEvent(String key, ProjectPayload payload, EventType eventType);

    void publishSectionEvent(String key, SectionPayload payload, EventType eventType);

    void publishMemberEvent(String key, MemberPayload payload, EventType eventType);
}
