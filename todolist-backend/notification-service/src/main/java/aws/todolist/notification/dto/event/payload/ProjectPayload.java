package aws.todolist.notification.dto.event.payload;

import aws.todolist.notification.dto.event.dto.ProjectEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectPayload extends PayloadBase {

    private ProjectEventDto project;
}
