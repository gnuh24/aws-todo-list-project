package aws.todolist.taskflow.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Thông tin dự án (Project)")
public class ProjectResponseDTO {

    @Schema(description = "Mã ID của dự án", example = "3a7f2b4c-5e2d-4a12-bc3e-8d6f7a1a9b2c")
    private String id;

    @Schema(description = "Tên dự án", example = "Quản lý công việc nhóm")
    private String name;

    @Schema(description = "Trạng thái đã lưu trữ (archive) hay chưa", example = "false")
    private Boolean isArchived;

    @Schema(description = "Thời điểm tạo dự án", example = "2025-10-06T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật dự án", example = "2025-10-06T09:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Project có phải dự án mặc định không")
    private Boolean isDefault;
}