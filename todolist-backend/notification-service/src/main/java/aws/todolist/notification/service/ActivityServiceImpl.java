package aws.todolist.notification.service;

import aws.todolist.notification.dto.activity.ActivityRequest;
import aws.todolist.notification.dto.activity.ActivityResponse;
import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.mapper.ActivityMapper;
import aws.todolist.notification.mapper.NotificationMapper;
import aws.todolist.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ActivityServiceImpl implements ActivityService{

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private ActivityMapper mapper;

    @Override
    public Map<LocalDate, List<ActivityResponse>> getActivityByCondition(ActivityRequest request) {

        List<Notification> notifications = repository.findDistinctActorAndType(request.getProjectIds(),request.getAccountIds(),request.getTypes());

        Map<LocalDate, List<ActivityResponse>> map = new LinkedHashMap<>();
        Set<String> seenKeys = new HashSet<>();

        for (Notification n : notifications) {
            LocalDateTime truncated = n.getCreatedAt().truncatedTo(ChronoUnit.MINUTES);
            String dedupKey = n.getActor().getId() + "|" + n.getType() + "|" + truncated;

            // nếu thêm thành công => chưa thấy trước đó
            if (seenKeys.add(dedupKey)) {
                LocalDate date = n.getCreatedAt().toLocalDate();
                map.computeIfAbsent(date, d -> new ArrayList<>()).add(mapper.toResponse(n));
            }
        }

        return map;
    }
}
