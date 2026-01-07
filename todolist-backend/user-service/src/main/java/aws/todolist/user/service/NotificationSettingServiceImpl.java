package aws.todolist.user.service;

import aws.todolist.user.dto.notificationSetting.NotificationSettingBulkUpdateRequest;
import aws.todolist.user.dto.notificationSetting.NotificationSettingResponse;
import aws.todolist.user.entity.NotificationSetting;
import aws.todolist.user.enums.NotificationType;
import aws.todolist.user.mapper.NotificationSettingMapper;
import aws.todolist.user.repository.NotificationSettingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationSettingServiceImpl implements NotificationSettingService {

    @Autowired
    private NotificationSettingRepository repository;

    @Autowired
    private NotificationSettingMapper mapper;

    @Override
    @Transactional
    public List<NotificationSettingResponse> getByAccountId(String accountId) {
        return mapper.toResponseList(
                repository.findByAccount_Id(accountId)
        );
    }


    @Override
    public List<NotificationSettingResponse> bulkUpdate(
            String accountId,
            NotificationSettingBulkUpdateRequest request
    ) {
        // Lấy toàn bộ setting hiện có của user
        List<NotificationSetting> settings = repository.findByAccount_Id(accountId);

        // Map nhanh theo type để update O(1)
        Map<NotificationType, NotificationSetting> settingMap =
                settings.stream()
                        .collect(Collectors.toMap(
                                NotificationSetting::getNotificationType,
                                Function.identity()
                        ));

        List<NotificationSetting> updatedSettings = new ArrayList<>();

        for (var item : request.getSettings()) {
            NotificationSetting setting = settingMap.get(item.getNotificationType());

            if (setting == null) {
                throw new EntityNotFoundException(
                        "SETTING_NOT_FOUND: " + item.getNotificationType()
                );
            }

            setting.setEnableWeb(item.getEnableWeb());
            setting.setEnableEmail(item.getEnableEmail());

            updatedSettings.add(setting);
        }


        // save 1 lần
        repository.saveAll(updatedSettings);

        // map entity -> response
        return mapper.toResponseList(updatedSettings);
    }
}
