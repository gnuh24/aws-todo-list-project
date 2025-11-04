package aws.todolist.taskflow.dto.project;

import aws.todolist.taskflow.dto.section.SectionResponseDTO;
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
@Schema(description = "Thông tin chi tiết của dự án")
public class ProjectDetailResponseDTO {

    @Schema(description = "Mã ID của dự án", example = "3a7f2b4c-5e2d-4a12-bc3e-8d6f7a1a9b2c")
    private String id;

    @Schema(description = "Tên dự án", example = "Quản lý công việc nhóm")
    private String name;

    @Schema(description = "Trạng thái đã lưu trữ", example = "false")
    private Boolean isArchived;

    @Schema(description = "Thời điểm tạo dự án")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật dự án")
    private LocalDateTime updatedAt;

    @Schema(description = "Danh sách section của dự án")
    private List<SectionResponseDTO> sections;

    @Schema(description = "Project có phải dự án mặc định không")
    private Boolean isDefault;
}
