package aws.todolist.user.repository;

import aws.todolist.user.entity.NotificationSetting;
import aws.todolist.user.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingRepository
        extends JpaRepository<NotificationSetting, String> {

    List<NotificationSetting> findByAccount_Id(String accountId);
}