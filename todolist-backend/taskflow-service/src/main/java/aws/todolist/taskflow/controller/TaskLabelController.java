package aws.todolist.taskflow.controller;

import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.taskLabel.TaskLabelRequestDTO;
import aws.todolist.taskflow.dto.taskLabel.TaskLabelResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Task Label API", description = "CRUD cho nhãn task")
public class TaskLabelController {
	
	@Autowired
	private LabelService labelService;
	
	@Operation(summary = "Lấy danh sách nhãn", description = "Lấy cả nhãn cá nhân và nhãn của project")
	@GetMapping("/{projectId}/labels")
	public ResponseEntity<ApiResponse<?>> getLabels(@PathVariable("projectId") String projectId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof Account)) {
			return ResponseEntity.status(401).body(new ApiResponse<>(401, "User not authenticated", null));
		}
		String userId = ((Account) principal).getId();
		
		var personalLabels = labelService.getPersonalLabels(userId);
		var projectLabels = labelService.getProjectLabels(projectId);
		
		var responseBody = Map.of(
		    "personalLabels", personalLabels,
		    "projectLabels", projectLabels
		);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy nhãn thành công", responseBody));
	}
	
	// ================= ADD TASK LABEL =================
	@Operation(summary = "Thêm nhãn cho Task", description = "Thêm một nhãn vào Task")
	@PostMapping("/{projectId}/tasks/{taskId}/labels")
	public ResponseEntity<ApiResponse<TaskLabelResponseDTO>> addTaskLabel(
	    @PathVariable("projectId") String projectId,
	    @PathVariable("taskId") String taskId,
	    @RequestBody @Valid TaskLabelRequestDTO requestDTO
	) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof Account)) {
			return ResponseEntity.status(401).body(new ApiResponse<>(401, "User not authenticated", null));
		}
		String userId = ((Account) principal).getId();
		
		TaskLabelResponseDTO taskLabel = labelService.addLabelToTask(projectId, taskId, requestDTO, (Account) principal);
		return ResponseEntity.ok(new ApiResponse<>(200, "Thêm nhãn thành công", taskLabel));
	}
	
	@Operation(summary = "Xóa nhãn khỏi Task", description = "Xóa nhãn của Task")
	@DeleteMapping("/{projectId}/tasks/{taskId}/labels/{labelId}")
	public ResponseEntity<ApiResponse<String>> deleteTaskLabel(
	    @PathVariable String projectId,
	    @PathVariable String taskId,
	    @PathVariable String labelId,
	    @AuthenticationPrincipal Account account
	) {
	
//		if (account == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//			    .body(new ApiResponse<>(401, "User not authenticated"));
//		}
		
		labelService.removeLabelFromTask(projectId, taskId, labelId);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa nhãn thành công", ":3"));
	}

}
