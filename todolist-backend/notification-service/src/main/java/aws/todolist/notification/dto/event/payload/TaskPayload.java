package aws.todolist.notification.dto.event.payload;

import aws.todolist.notification.dto.event.dto.TaskEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPayload extends PayloadBase {

    private TaskEventDto task;
}
