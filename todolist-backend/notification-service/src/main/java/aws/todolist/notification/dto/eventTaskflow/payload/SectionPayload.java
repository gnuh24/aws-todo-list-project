package aws.todolist.notification.dto.eventTaskflow.payload;

import aws.todolist.notification.dto.eventTaskflow.dto.SectionEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SectionPayload extends PayloadBase {

    private SectionEventDto section;
}
