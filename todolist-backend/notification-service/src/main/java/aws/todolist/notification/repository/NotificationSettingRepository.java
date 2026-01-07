package aws.todolist.notification.repository;

import aws.todolist.notification.entity.NotificationSetting;
import aws.todolist.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, String> {

    NotificationSetting findByNotificationTypeAndAccountId(
            NotificationType type,
            String accountId
    );

}