package aws.todolist.notification.messaging.kafka.resolver;

import aws.todolist.notification.dto.eventTaskflow.EventEnvelope;
import aws.todolist.notification.dto.eventTaskflow.payload.TaskPayload;
import aws.todolist.notification.enums.NotificationType;
import aws.todolist.notification.enums.Status;
import org.springframework.stereotype.Component;

@Component
public class NotificationResolver {

    public NotificationType resolve(EventEnvelope<?> event) {

        return switch (event.getEventType()) {

            // ================= PROJECT =================
            case PROJECT_MEMBER_ADDED,
                 PROJECT_MEMBER_ROLE_UPDATED,
                 PROJECT_DELETED -> resolveProject(event);

            // ================= REQUEST / MEMBER =================
            case PROJECT_MEMBER_ACCEPTED,
                 PROJECT_MEMBER_DECLINED -> resolveRequest(event);

            // ================= TASK =================
            case TASK_ASSIGNED,
                 TASK_STATUS_UPDATED,
                 TASK_DUE_SOON,
                 TASK_OVERDUE,
                 TASK_UPDATED-> resolveTask(event);

            // ================= COMMENT =================
            case COMMENT_CREATED -> NotificationType.TASK_COMMENTED;

            // ================= DEFAULT =================
            default -> null;
        };
    }

    private NotificationType resolveProject(EventEnvelope<?> event) {

        return switch (event.getEventType()) {
            case PROJECT_MEMBER_ADDED -> NotificationType.PROJECT_MEMBER_ADDED;
            case PROJECT_MEMBER_ROLE_UPDATED -> NotificationType.PROJECT_MEMBER_ROLE_UPDATED;
            case PROJECT_DELETED -> NotificationType.PROJECT_DELETED;
            default -> null;
        };
    }

    private NotificationType resolveRequest(EventEnvelope<?> event) {

        return switch (event.getEventType()) {
            case PROJECT_MEMBER_ACCEPTED -> NotificationType.REQUEST_ACCEPTED;
            case PROJECT_MEMBER_DECLINED -> NotificationType.REQUEST_DECLINED;
            default -> null;
        };
    }

    private NotificationType resolveTask(EventEnvelope<?> event) {

        return switch (event.getEventType()) {

            case TASK_ASSIGNED -> NotificationType.TASK_ASSIGNED;

            case TASK_DUE_SOON -> NotificationType.TASK_DUE_SOON;
            case TASK_OVERDUE -> NotificationType.TASK_OVERDUE;

            case TASK_STATUS_UPDATED -> resolveCompletedOnly(event);

            case TASK_UPDATED ->  NotificationType.TASK_UPDATED;

            default -> null;
        };
    }

    private NotificationType resolveCompletedOnly(EventEnvelope<?> event) {

        TaskPayload payload = (TaskPayload) event.getPayload();

        if (payload.getTask().getStatus() == Status.COMPLETED) {
            return NotificationType.TASK_COMPLETED;
        }

        return null;
    }


}
