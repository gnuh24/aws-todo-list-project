package aws.todolist.notification.dto.eventTaskflow.payload;

import aws.todolist.notification.dto.eventTaskflow.dto.CommentEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPayload extends PayloadBase {

    private CommentEventDto comment;
}
