package aws.todolist.notification.dto.eventTaskflow.payload;

import aws.todolist.notification.dto.eventTaskflow.dto.ProjectEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectPayload extends PayloadBase {

    private ProjectEventDto project;
}
