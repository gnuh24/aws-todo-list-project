package aws.todolist.taskflow.dto.event.payload;

import aws.todolist.taskflow.dto.event.PayloadBase;
import aws.todolist.taskflow.dto.event.dto.CommentEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPayload extends PayloadBase {

    private CommentEventDto comment;
}
