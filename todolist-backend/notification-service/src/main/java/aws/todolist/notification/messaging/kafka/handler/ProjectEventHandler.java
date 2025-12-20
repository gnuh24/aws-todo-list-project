package aws.todolist.notification.messaging.kafka.handler;

import aws.todolist.notification.dto.event.EventEnvelope;
import aws.todolist.notification.dto.event.payload.ProjectPayload;
import aws.todolist.notification.enums.EventType;
import aws.todolist.notification.enums.NotificationType;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.messaging.kafka.resolver.NotificationResolver;
import aws.todolist.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProjectEventHandler implements EventHandler<ProjectPayload> {

    private final NotificationService notificationService;
    private final NotificationResolver resolver;

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.PROJECT_DELETED;
    }

    @Override
    public void handle(EventEnvelope<ProjectPayload> event) {

        ProjectPayload payload = event.getPayload();

        NotificationType type = resolver.resolve(event);

        if (type == null || payload.getReceivers() == null) {
            return;
        }

        for (String receiverEmail : payload.getReceivers()) {

            NotificationMessage message = new NotificationMessage();

            message.setReceiverEmail(receiverEmail);
            message.setActorId(payload.getActor().getId());
            message.setProjectId(payload.getProjectId());
            message.setTaskId(null);
            message.setType(type);

            // ⭐ GÁN TITLE + CONTENT Ở ĐÂY
            applyContent(message);

            notificationService.create(message);
        }
    }

    private void applyContent(NotificationMessage message) {

        if (message.getType() != NotificationType.PROJECT_DELETED) {
            return;
        }

        message.setTitle("Project đã bị xoá");
        message.setContent("Một project bạn tham gia đã bị xoá.");
    }


}
