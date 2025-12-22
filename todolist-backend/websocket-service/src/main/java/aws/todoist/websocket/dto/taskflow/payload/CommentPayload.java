package aws.todoist.websocket.dto.taskflow.payload;

import aws.todoist.websocket.dto.taskflow.dto.CommentEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPayload extends PayloadBase {

    private CommentEventDto comment;
}
