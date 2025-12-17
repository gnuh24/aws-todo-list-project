package aws.todoist.websocket.dto.event.payload;

import aws.todoist.websocket.dto.event.dto.ProjectEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectPayload extends PayloadBase {

    private ProjectEventDto project;
}
