package aws.todoist.websocket.dto.notification;

import aws.todoist.websocket.enums.eventDto.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    
    private String id;
    private String title;
    private String content;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
    
    private String actorId;
    private String projectId;
    private String taskId;
    private String displayName;
    private String avatar;
}