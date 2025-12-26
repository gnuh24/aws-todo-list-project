package aws.todolist.taskflow.dto.event.payload;

import aws.todolist.taskflow.dto.event.PayloadBase;
import aws.todolist.taskflow.dto.event.dto.TaskEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPayload extends PayloadBase {

    private TaskEventDto task;
}
