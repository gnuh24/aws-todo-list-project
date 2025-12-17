package aws.todoist.websocket.dto.event.payload;

import aws.todoist.websocket.dto.event.dto.SectionEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SectionPayload extends PayloadBase {

    private SectionEventDto section;
}
