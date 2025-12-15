package aws.todolist.notification.dto.notification;

import aws.todolist.notification.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WebSocketResponse {

    private String id;
    private String title;
    private String content;
    private Notification.NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
    private String receiverId;
    private String actorId;
    private String projectId;
    private String taskId;
    private String displayName;
    private String avatar;
}
