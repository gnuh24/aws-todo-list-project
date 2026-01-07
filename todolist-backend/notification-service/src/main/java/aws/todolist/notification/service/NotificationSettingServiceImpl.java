package aws.todolist.notification.service;

import aws.todolist.notification.entity.Account;
import aws.todolist.notification.entity.NotificationSetting;
import aws.todolist.notification.enums.NotificationType;
import aws.todolist.notification.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSettingServiceImpl implements NotificationSettingService{

    @Autowired
    private NotificationSettingRepository repository;


    @Override
    public NotificationSetting getByAccountIdAndType(Account account, NotificationType type) {
        return repository.findByNotificationTypeAndAccountId(type, account.getId());
    }
}
