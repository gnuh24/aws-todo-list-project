package aws.todolist.notification.mapper;

import aws.todolist.notification.dto.event.EventEnvelope;
import aws.todolist.notification.dto.event.payload.PayloadBase;
import aws.todolist.notification.enums.NotificationType;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationMessageMapper {

    public <T extends PayloadBase> List<NotificationMessage> fromEvent(
            EventEnvelope<T> event,
            NotificationType type
    ) {

        List<NotificationMessage> result = new ArrayList<>();

        T payload = event.getPayload();

        // Nếu không có người nhận → không tạo notification
        if (payload.getReceivers() == null || payload.getReceivers().isEmpty()) {
            return result;
        }

        for (String receiverId : payload.getReceivers()) {

            NotificationMessage message = new NotificationMessage();
            message.setReceiverEmail(receiverId);
            message.setActorId(payload.getActor().getId());
            message.setProjectId(payload.getProjectId());
            message.setType(type);

            result.add(message);
        }

        return result;
    }
}