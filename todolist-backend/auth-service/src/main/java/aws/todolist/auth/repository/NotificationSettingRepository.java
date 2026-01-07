package aws.todolist.auth.repository;

import aws.todolist.auth.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, String> {

    boolean existsByAccountId(String accountId);

    List<NotificationSetting> findAllByAccountId(String accountId);
}
