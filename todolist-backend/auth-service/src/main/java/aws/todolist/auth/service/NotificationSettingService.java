package aws.todolist.auth.service;

import aws.todolist.auth.entity.Account;
import org.springframework.stereotype.Service;

@Service
public interface NotificationSettingService {
    /**
     * Tạo default notification setting cho account
     * - 1 record / NotificationType
     * - dùng khi tạo account mới
     */
    void createDefaultForAccount(Account account);
}
