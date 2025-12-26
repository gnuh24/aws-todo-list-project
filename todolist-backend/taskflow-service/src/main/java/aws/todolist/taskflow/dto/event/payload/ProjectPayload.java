package aws.todolist.taskflow.dto.event.payload;

import aws.todolist.taskflow.dto.event.PayloadBase;
import aws.todolist.taskflow.dto.event.dto.ProjectEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectPayload extends PayloadBase {

    private ProjectEventDto project;
}
