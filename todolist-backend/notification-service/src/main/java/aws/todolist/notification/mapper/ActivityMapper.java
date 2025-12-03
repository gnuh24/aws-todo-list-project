package aws.todolist.notification.mapper;

import aws.todolist.notification.dto.activity.ActivityResponse;
import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Notification;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Notification activity);

    @AfterMapping
    default void handleActorFields(Notification notification, @MappingTarget ActivityResponse dto) {
        if (notification.getActor() != null) {
            dto.setActorId(notification.getActor().getId());
            dto.setDisplayName(notification.getActor().getDisplayName());
            dto.setAvatar(notification.getActor().getAvatar());
        }
    }
}
