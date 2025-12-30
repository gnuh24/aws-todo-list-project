package aws.todolist.project.dto.taskComment;


import aws.todolist.project.dto.commentAttach.CommentAttachResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết bình luận của Task")
public class TaskCommentResponseDTO {

    @Schema(description = "Mã ID của bình luận", example = "comment001")
    private String id;

    @Schema(description = "Mã ID của Task mà bình luận thuộc về", example = "task001")
    private String taskId;

    @Schema(description = "Mã ID của người bình luận", example = "account001")
    private String accountId;

    @Schema(description = "Tên người bình luận", example = "Lê Trung Kiên")
    private String authorName;

    @Schema(description = "Avatar người bình luận", example = "https://www.flaticon.com/free-icon/avatar_147142")
    private String authorAvatar;

    @Schema(description = "Nội dung bình luận", example = "Cần cập nhật deadline cho chính xác")
    private String comment;

    @Schema(description = "Thời điểm bình luận được tạo", example = "2025-10-09T09:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm bình luận được cập nhật", example = "2025-10-09T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Danh sách các attachment của commet")
    private List<CommentAttachResponse> commentAttach;
}
