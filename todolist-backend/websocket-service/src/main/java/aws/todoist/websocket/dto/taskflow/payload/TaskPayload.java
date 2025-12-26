package aws.todoist.websocket.dto.taskflow.payload;

import aws.todoist.websocket.dto.taskflow.dto.TaskEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPayload extends PayloadBase {

    private TaskEventDto task;
}
