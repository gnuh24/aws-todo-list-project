package aws.todolist.taskflow.dto.section;

import aws.todolist.taskflow.dto.task.TaskResponseDTO;
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
@Schema(description = "Thông tin Section")
public class SectionResponseDTO {

    @Schema(description = "Mã ID của Section", example = "sec001")
    private String id;

    @Schema(description = "Tên Section", example = "To-do")
    private String name;

    @Schema(description = "Vị trí của Section trong Project", example = "1")
    private Integer position;

    @Schema(description = "Trạng thái đã lưu trữ (archive) hay chưa", example = "false")
    private Boolean isArchived;

    @Schema(description = "Thời điểm tạo Section")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật Section")
    private LocalDateTime updatedAt;

    @Schema(description = "Danh sách Task của Section")
    private List<TaskResponseDTO> tasks;
}