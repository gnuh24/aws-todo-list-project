package aws.todolist.auth.service;

import aws.todolist.auth.entity.Account;
import aws.todolist.auth.entity.NotificationSetting;
import aws.todolist.auth.enums.NotificationType;
import aws.todolist.auth.repository.NotificationSettingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationSettingServiceImpl implements NotificationSettingService {

    @Autowired
    private NotificationSettingRepository settingRepository;

    @Override
    @Transactional
    public void createDefaultForAccount(Account account) {

        // tránh tạo trùng (an toàn nếu retry)
        boolean existed =
                settingRepository.existsByAccountId(account.getId());

        if (existed) {
            return;
        }

        List<NotificationSetting> settings =
                Arrays.stream(NotificationType.values())
                        .map(type ->
                                NotificationSetting.defaultFor(account, type)
                        )
                        .toList();

        settingRepository.saveAll(settings);
    }


}