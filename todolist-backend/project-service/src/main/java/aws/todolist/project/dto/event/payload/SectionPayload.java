package aws.todolist.project.dto.event.payload;

import aws.todolist.project.dto.event.PayloadBase;
import aws.todolist.project.dto.event.dto.SectionEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SectionPayload extends PayloadBase {

    private SectionEventDto section;
}
