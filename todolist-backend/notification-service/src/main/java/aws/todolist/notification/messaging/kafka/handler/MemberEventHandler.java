package aws.todolist.notification.messaging.kafka.handler;

import aws.todolist.notification.dto.event.EventEnvelope;
import aws.todolist.notification.dto.event.payload.MemberPayload;
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
public class MemberEventHandler implements EventHandler<MemberPayload> {

    private final NotificationService notificationService;
    private final NotificationResolver resolver;

    @Override
    public boolean supports(EventType eventType) {
        return switch (eventType) {
            case PROJECT_MEMBER_ADDED,
                 PROJECT_MEMBER_ROLE_UPDATED,
                 PROJECT_MEMBER_ACCEPTED,
                 PROJECT_MEMBER_DECLINED -> true;
            default -> false;
        };
    }

    @Override
    public void handle(EventEnvelope<MemberPayload> event) {

        MemberPayload payload = event.getPayload();
        NotificationType type = resolver.resolve(event);

        if (type == null || payload.getReceivers() == null) {
            return;
        }

        for (String receiverEmail : payload.getReceivers()) {

            NotificationMessage message = new NotificationMessage();
            message.setReceiverEmail(receiverEmail);
            message.setActorId(payload.getActor().getId());
            message.setProjectId(payload.getProjectId());
            message.setType(type);

            applyContent(message);


            notificationService.create(message);
        }
    }

    private void applyContent(NotificationMessage message) {

        switch (message.getType()) {

            case PROJECT_MEMBER_ADDED -> {
                message.setTitle("Bạn được thêm vào project");
                message.setContent("Bạn vừa được thêm vào một project.");
            }

            case PROJECT_MEMBER_ROLE_UPDATED -> {
                message.setTitle("Vai trò trong project thay đổi");
                message.setContent("Vai trò của bạn trong project đã được cập nhật.");
            }

            case REQUEST_ACCEPTED -> {
                message.setTitle("Yêu cầu được chấp nhận");
                message.setContent("Lời mời tham gia project đã được chấp nhận.");
            }

            case REQUEST_DECLINED -> {
                message.setTitle("Yêu cầu bị từ chối");
                message.setContent("Lời mời tham gia project đã bị từ chối.");
            }

            default -> {
                // ignore
            }
        }
    }
}