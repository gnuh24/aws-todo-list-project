package aws.todolist.taskflow.dto.task;


import aws.todolist.taskflow.dto.member.MemberDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.dto.taskLabel.TaskLabelResponseDTO;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết của một Task")
public class TaskDetailResponseDTO {
	
	@Schema(description = "Mã ID của Task", example = "task001")
	private String id;
	
	@Schema(description = "Tiêu đề của Task", example = "Hoàn thành tài liệu thiết kế hệ thống")
	private String title;
	
	@Schema(description = "Mô tả chi tiết nội dung của Task", example = "Cần hoàn thành phần mô tả API và sequence diagram")
	private String description;
	
	@Schema(description = "Trạng thái lưu trữ của Task", example = "false")
	private Boolean isArchived;
	
	@Schema(description = "Trạng thái ghim Task", example = "true")
	private Boolean isPinned;
	
	@Schema(description = "Trạng thái hiện tại của Task", example = "IN_PROGRESS")
	private Status status;
	
	@Schema(description = "Độ ưu tiên của Task", example = "HIGH")
	private Priority priority;
	
	@Schema(description = "Hạn hoàn thành của Task", example = "2025-10-15T18:00:00")
	private LocalDateTime deadline;
	
	@Schema(description = "Thời gian bắt đầu thực hiện Task", example = "2025-10-10T09:00:00")
	private LocalDateTime startTime;
	
	@Schema(description = "Thời điểm Task hoàn thành", example = "2025-10-12T14:30:00")
	private LocalDateTime completedAt;
	
	@Schema(description = "Thời điểm Task được tạo", example = "2025-10-09T08:00:00")
	private LocalDateTime createdAt;
	
	@Schema(description = "Thời điểm Task được cập nhật gần nhất", example = "2025-10-09T10:00:00")
	private LocalDateTime updatedAt;
	
	@Schema(description = "id người tạo", example = "")
	private MemberDTO createdByAccount;
	
	@Schema(description = "id người được phân công", example = "")
	private MemberDTO accountAssign;
	
	@Schema(description = "Danh sách các Task con")
	private List<TaskResponseDTO> taskChild;
	
	@Schema(description = "Danh sách các bình luận trong Task")
	private List<TaskCommentResponseDTO> comments;
	
	@Schema(description = "Danh sách nhãn (label) của Task")
	private List<TaskLabelResponseDTO> labels;
	
	@Schema(description = "Section của task")
	private String idSection;
	
	@Schema(description = "Project chứa task")
	private String idProject;
}

