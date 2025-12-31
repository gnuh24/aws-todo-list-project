package aws.todolist.taskflow.dto.task;

import aws.todolist.taskflow.dto.member.MemberDTO;
import aws.todolist.taskflow.enums.Priority;
import aws.todolist.taskflow.enums.Status;
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
@Schema(description = "Thông tin Task")
public class TaskResponseDTO {

    @Schema(description = "Mã ID của Task", example = "task001")
    private String id;

    @Schema(description = "Tiêu đề Task", example = "Hoàn thành báo cáo")
    private String title;

    @Schema(description = "Mô tả chi tiết nội dung của Task", example = "Cần hoàn thành phần mô tả API và sequence diagram")
    private String description;

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

    @Schema(description = "Task cha")
    private String idTaskCha;

    @Schema(description = "id người tạo", example = "")
    private String idAccountCreate;

    @Schema(description = "id người được phân công", example = "")
    private String idAccountAssigned;
	
	private MemberDTO accountAssign;

    @Schema(description = "idSection của task")
    private String idSection;

    @Schema(description = "idProject chứa task")
    private String idProject;
	
	private String sectionName;
	
	private String projectName;


}
