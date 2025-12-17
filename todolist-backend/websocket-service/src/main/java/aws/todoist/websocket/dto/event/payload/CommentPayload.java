package aws.todoist.websocket.dto.event.payload;

import aws.todoist.websocket.dto.event.dto.CommentEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPayload extends PayloadBase {

    private CommentEventDto comment;
}
