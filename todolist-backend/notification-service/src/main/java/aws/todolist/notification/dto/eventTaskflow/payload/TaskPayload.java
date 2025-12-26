package aws.todolist.notification.dto.eventTaskflow.payload;

import aws.todolist.notification.dto.eventTaskflow.dto.TaskEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPayload extends PayloadBase {

    private TaskEventDto task;
}
