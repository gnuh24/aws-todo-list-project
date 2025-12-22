package aws.todolist.notification.messaging.kafka.handler;

import aws.todolist.notification.dto.eventTaskflow.EventEnvelope;
import aws.todolist.notification.dto.eventTaskflow.payload.TaskPayload;
import aws.todolist.notification.enums.EventType;
import aws.todolist.notification.enums.NotificationType;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.messaging.kafka.resolver.NotificationResolver;
import aws.todolist.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventHandler implements EventHandler<TaskPayload> {

    private final NotificationService notificationService;
    private final NotificationResolver resolver;

    @Override
    public boolean supports(EventType eventType) {
        return switch (eventType) {
            case TASK_ASSIGNED,
                 TASK_UPDATED,
                 TASK_STATUS_UPDATED,
                 TASK_DUE_SOON,
                 TASK_OVERDUE -> true;
            default -> false;
        };
    }


    @Override
    public void handle(EventEnvelope<TaskPayload> event) {

        TaskPayload payload = (TaskPayload) event.getPayload();

        NotificationType type = resolver.resolve(event);
        if (type == null) {
            return;
        }


        if (payload.getReceivers() == null || payload.getReceivers().isEmpty()) {
            return;
        }

        for (String receiverEmail : payload.getReceivers()) {

            NotificationMessage message = new NotificationMessage();

            message.setReceiverEmail(receiverEmail);
            message.setActorId(payload.getActor().getId());
            message.setProjectId(payload.getProjectId());
            message.setTaskId(payload.getTask().getId());
            message.setType(type);

            // ⭐ GÁN TITLE + CONTENT Ở ĐÂY
            applyContent(message);

            notificationService.create(message);
        }
    }

    private void applyContent(NotificationMessage message) {

        switch (message.getType()) {

            case TASK_ASSIGNED -> {
                message.setTitle("Bạn được giao task mới");
                message.setContent("Bạn vừa được giao một task trong project.");
            }

            case TASK_COMPLETED -> {
                message.setTitle("Task đã hoàn thành");
                message.setContent("Một task bạn liên quan vừa được hoàn thành.");
            }

            case TASK_DUE_SOON -> {
                message.setTitle("Task sắp đến hạn");
                message.setContent("Task của bạn sắp đến hạn hoàn thành.");
            }

            case TASK_OVERDUE -> {
                message.setTitle("Task đã quá hạn");
                message.setContent("Task của bạn đã quá hạn.");
            }

            case TASK_UPDATED -> {
                message.setTitle("Task được cập nhật");
                message.setContent("Một task bạn liên quan vừa được cập nhật.");
            }

            default -> {
                // không set gì → service vẫn có thể bỏ qua
            }
        }
    }
}
