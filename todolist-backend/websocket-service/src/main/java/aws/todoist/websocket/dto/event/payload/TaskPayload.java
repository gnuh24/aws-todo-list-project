package aws.todoist.websocket.dto.event.payload;

import aws.todoist.websocket.dto.event.dto.TaskEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPayload extends PayloadBase {

    private TaskEventDto task;
}
