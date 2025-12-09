package aws.todolist.notification.dto.activity;

import aws.todolist.notification.entity.Notification;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ActivityRequest {

    @NotNull
    private List<String> projectIds;

    @NotNull
    private List<String> accountIds;

    @NotNull
    private List<Notification.NotificationType> types;
}
