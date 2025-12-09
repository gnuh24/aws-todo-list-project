package aws.todolist.notification.service;

import aws.todolist.notification.dto.activity.ActivityRequest;
import aws.todolist.notification.dto.activity.ActivityResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface ActivityService {
    Map<LocalDate, List<ActivityResponse>> getActivityByCondition(ActivityRequest request);
}
