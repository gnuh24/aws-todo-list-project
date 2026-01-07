package aws.todolist.notification.service;

import aws.todolist.notification.entity.Account;
import aws.todolist.notification.entity.NotificationSetting;
import aws.todolist.notification.enums.NotificationType;
import org.springframework.stereotype.Service;

@Service
public interface NotificationSettingService {
    NotificationSetting getByAccountIdAndType(Account account, NotificationType type);
}
