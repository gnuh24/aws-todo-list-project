package aws.todolist.notification.messaging.kafka.handler;

import aws.todolist.notification.dto.eventTaskflow.EventEnvelope;
import aws.todolist.notification.dto.eventTaskflow.payload.CommentPayload;
import aws.todolist.notification.enums.EventType;
import aws.todolist.notification.enums.NotificationType;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.messaging.kafka.resolver.NotificationResolver;
import aws.todolist.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventHandler implements EventHandler<CommentPayload> {

    private final NotificationService notificationService;
    private final NotificationResolver resolver;

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.COMMENT_CREATED;
    }

    @Override
    public void handle(EventEnvelope<CommentPayload> event) {

        CommentPayload payload = event.getPayload();

        NotificationType type = resolver.resolve(event);

        if (type == null || payload.getReceivers() == null) {
            return;
        }

        for (String receiverEmail : payload.getReceivers()) {

            NotificationMessage message = new NotificationMessage();

            message.setReceiverEmail(receiverEmail);
            message.setActorId(payload.getActor().getId());
            message.setProjectId(payload.getProjectId());
            message.setTaskId(payload.getComment().getTaskId());
            message.setType(type);

            applyContent(message);

            notificationService.create(message);
        }
    }

    private void applyContent(NotificationMessage message) {

        if (message.getType() == NotificationType.TASK_COMMENTED) {
            message.setTitle("Task có bình luận mới");
            message.setContent("Có người vừa bình luận vào task bạn liên quan.");
        }
    }
}
