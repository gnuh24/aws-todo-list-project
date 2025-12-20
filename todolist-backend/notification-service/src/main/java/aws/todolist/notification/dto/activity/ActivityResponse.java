package aws.todolist.notification.dto.activity;

import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityResponse {

    private String id;
    private String content;
    private NotificationType type;
    private String projectId;
    private LocalDateTime createdAt;
    private String actorId;
    private String taskId;
    private String displayName;
    private String avatar;
}
