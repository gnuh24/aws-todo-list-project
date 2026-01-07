package aws.todolist.user.dto.notificationSetting;

import aws.todolist.user.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingBulkUpdateRequest {

    @NotNull
    private List<NotificationSettingItem> settings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettingItem {

        @NotNull
        private NotificationType notificationType;

        @NotNull
        private Boolean enableWeb;

        @NotNull
        private Boolean enableEmail;
    }
}
