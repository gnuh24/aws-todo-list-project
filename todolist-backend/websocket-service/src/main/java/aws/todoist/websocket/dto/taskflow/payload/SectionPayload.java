package aws.todoist.websocket.dto.taskflow.payload;

import aws.todoist.websocket.dto.taskflow.dto.SectionEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SectionPayload extends PayloadBase {

    private SectionEventDto section;
}
