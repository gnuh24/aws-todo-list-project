package aws.todolist.taskflow.dto.task;

import aws.todolist.taskflow.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO để tạo mới Task")
public class TaskCreateRequestDTO {

    @NotBlank(message = "Tiêu đề Task không được để trống")
    @Schema(description = "Tiêu đề của Task", example = "Hoàn thành tài liệu thiết kế")
    private String title;

    @Schema(description = "Mô tả chi tiết của Task", example = "Cần hoàn thiện phần API và sequence diagram")
    private String description;

    @Schema(description = "Mức độ ưu tiên của Task", example = "HIGH")
    private Priority priority;

    @Schema(description = "Thời hạn hoàn thành Task", example = "2025-10-15T18:00:00")
    private LocalDateTime deadline;

    @Schema(description = "Thời gian bắt đầu Task", example = "2025-10-10T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "ID của Task cha nếu là task con", example = "44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    private String taskFatherId;

    @Schema(description = "ID của Section chứa Task", example = "section001")
    private String sectionId;

    @Schema(description = "Task có được ghim hay không", example = "false")
    private Boolean isPinned;

    @Schema(description = "Task có được lưu trữ hay không", example = "false")
    private Boolean isArchived;

    @Schema(description = "Phân công task cho member", example = "id của account")
    private String idAccountAssign;
}