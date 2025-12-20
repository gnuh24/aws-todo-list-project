package aws.todolist.notification.dto.event.payload;

import aws.todolist.notification.dto.event.dto.CommentEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPayload extends PayloadBase {

    private CommentEventDto comment;
}
