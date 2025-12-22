package aws.todolist.notification.messaging.kafka.handler;

import aws.todolist.notification.dto.eventTaskflow.EventEnvelope;
import aws.todolist.notification.enums.EventType;

public interface EventHandler<T> {

    boolean supports(EventType eventType);

    void handle(EventEnvelope<T> event);
}