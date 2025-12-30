package aws.todolist.project.dto.event.payload;

import aws.todolist.project.dto.event.PayloadBase;
import aws.todolist.project.dto.event.dto.ProjectEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectPayload extends PayloadBase {

    private ProjectEventDto project;
}
