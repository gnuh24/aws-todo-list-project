package aws.todolist.project.dto.commentAttach;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết đính kèm của bình luận")
public class CommentAttachResponse {

    @Schema(description = "ID của file đính kèm trong bình luận", example = "attach_123456")
    private String id;

    @Schema(description = "URL của file đính kèm", example = "https://s3.amazonaws.com/bucket/comment/temp/photo.png")
    private String attachmentUrl;

    @Schema(description = "Thời điểm file được tải lên", example = "2025-11-15T14:32:10")
    private LocalDateTime createdAt;

    @Schema(description = "ID của bình luận chứa file đính kèm", example = "comment_001")
    private String taskCommentId;

}


