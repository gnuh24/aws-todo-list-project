package aws.todolist.taskflow.dto.event.dto;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEventDto {

    private String id;

    private String comment;

    private String taskId;

    private String accountId;

    private String authorName;

    private String authorAvatar;

    private List<CommentAttachResponse> commentAttach;
}
