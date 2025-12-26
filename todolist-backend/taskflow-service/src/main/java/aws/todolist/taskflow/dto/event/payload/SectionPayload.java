package aws.todolist.taskflow.dto.event.payload;

import aws.todolist.taskflow.dto.event.PayloadBase;
import aws.todolist.taskflow.dto.event.dto.SectionEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SectionPayload extends PayloadBase {

    private SectionEventDto section;
}
