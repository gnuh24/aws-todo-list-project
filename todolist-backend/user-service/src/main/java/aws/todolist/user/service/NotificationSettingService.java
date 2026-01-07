package aws.todolist.user.service;

import aws.todolist.user.dto.notificationSetting.NotificationSettingBulkUpdateRequest;
import aws.todolist.user.dto.notificationSetting.NotificationSettingResponse;
import aws.todolist.user.enums.NotificationType;

import java.util.List;

public interface NotificationSettingService {

    List<NotificationSettingResponse> getByAccountId(String accountId);

    List<NotificationSettingResponse> bulkUpdate(
            String accountId,
            NotificationSettingBulkUpdateRequest request
    );
}
