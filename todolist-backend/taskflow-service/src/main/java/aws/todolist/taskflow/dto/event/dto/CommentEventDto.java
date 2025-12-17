package aws.todolist.taskflow.dto.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEventDto {

    private String id;
    private String content;

    private String taskId;

    private String authorId;
    private String authorName;
}
