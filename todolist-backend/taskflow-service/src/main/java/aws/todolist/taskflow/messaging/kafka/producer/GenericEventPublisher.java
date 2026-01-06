package aws.todolist.taskflow.messaging.kafka.producer;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.dto.event.payload.*;
import aws.todolist.taskflow.enums.EventType;
import org.springframework.stereotype.Service;

@Service
public interface GenericEventPublisher {
    void publishProjectEvent(String key, ProjectPayload payload, EventType eventType);

    void publishTaskEvent(String key, TaskPayload payload, EventType eventType);

    void publishCommentEvent(String key, CommentPayload payload, EventType eventType);

    void publishSectionEvent(String key, SectionPayload payload, EventType eventType);

    void publishMemberEvent(String key, MemberPayload payload, EventType eventType);

    void publishCommentAttachEvent(
            String key,
            CommentAttachResponse attach,
            EventType eventType
    );

}
