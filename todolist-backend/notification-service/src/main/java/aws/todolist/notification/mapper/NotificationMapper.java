package aws.todolist.notification.mapper;

import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    // Phương thức Helper để ánh xạ Entity sang DTO
    public NotificationResponse mapToResponse(Notification notification) {
        // Logic mapping...
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                // Lấy ID từ entity
                .actorId(notification.getActor() != null ? notification.getActor().getId() : null)
                .projectId(notification.getProjectId())
                .taskId(notification.getTaskId())
                .build();
    }
}
