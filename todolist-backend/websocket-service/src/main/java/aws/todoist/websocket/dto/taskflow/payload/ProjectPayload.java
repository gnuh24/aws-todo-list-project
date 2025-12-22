package aws.todoist.websocket.dto.taskflow.payload;

import aws.todoist.websocket.dto.taskflow.dto.ProjectEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectPayload extends PayloadBase {

    private ProjectEventDto project;
}
