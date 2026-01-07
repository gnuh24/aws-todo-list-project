package aws.todolist.user.mapper;

import aws.todolist.user.dto.notificationSetting.NotificationSettingResponse;
import aws.todolist.user.entity.NotificationSetting;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface NotificationSettingMapper {

    NotificationSettingResponse toResponse(NotificationSetting entity);

    List<NotificationSettingResponse> toResponseList(
            List<NotificationSetting> entities
    );
}
