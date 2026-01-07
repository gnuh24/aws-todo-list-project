package aws.todolist.user.dto.notificationSetting;

import aws.todolist.user.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
@AllArgsConstructor
public class NotificationSettingResponse {

    private NotificationType notificationType;
    private boolean enableWeb;
    private boolean enableEmail;
}