package aws.todolist.taskflow.dto.taskComment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO để tạo mới TaskComment")
public class TaskCommentRequestDTO {

    @Schema(description = "Nội dung comment", example = "This is a comment")
    private String comment;

    @Schema(description = "Danh sách các url cho comment attach")
    private String[] urls;
}
