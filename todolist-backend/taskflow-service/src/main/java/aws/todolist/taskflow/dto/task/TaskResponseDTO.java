package aws.todolist.taskflow.dto.task;

import aws.todolist.taskflow.enums.Priority;
import aws.todolist.taskflow.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Thông tin Task")
public class TaskResponseDTO {

    @Schema(description = "Mã ID của Task", example = "task001")
    private String id;

    @Schema(description = "Tiêu đề Task", example = "Hoàn thành báo cáo")
    private String title;

    @Schema(description = "Task có được ghim (pinned) hay không", example = "true")
    private Boolean isPinned;

    @Schema(description = "Trạng thái Task", example = "TODO")
    private Status status;

    @Schema(description = "Độ ưu tiên Task", example = "HIGH")
    private Priority priority;

    @Schema(description = "Thời hạn hoàn thành Task")
    private LocalDateTime deadline;

    @Schema(description = "Thời gian bắt đầu task")
    private LocalDateTime startTime;

    @Schema(description = "Thời điểm tạo Task")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật Task")
    private LocalDateTime updatedAt;

    @Schema(description = "Danh sách sub-task")
    private List<TaskResponseDTO> taskChild;

    @Schema(description = "Task cha")
    private String idTaskCha;
}
