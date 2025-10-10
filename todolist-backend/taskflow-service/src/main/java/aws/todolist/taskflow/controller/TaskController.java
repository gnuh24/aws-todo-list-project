package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.task.*;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Task API", description = "CRUD của task")
// TODO: Quyền OWNER, MEMBER
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(summary = "Lấy ra chi tiết task", description = "Lấy ra thông tin chi tiết của task")
    @GetMapping("/{idProject}/tasks/{idTask}")
    @RequireProjectRole({Role.OWNER, Role.ADMIN, Role.MEMBER, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskDetailResponseDTO>> getTaskByTaskId(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask) {

        TaskDetailResponseDTO taskDetailResponseDTO = taskService.getTaskById(idTask);

        ApiResponse<TaskDetailResponseDTO> response = new ApiResponse<>(200, "Task has fetched successfully", taskDetailResponseDTO);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Tạo task mới", description = "Tạo thêm một task mới")
    @PostMapping("/{idProject}/tasks")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> addNewTask(@PathVariable("idProject") String idProject, @RequestBody @Valid TaskCreateRequestDTO requestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.addTask(idProject, requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has been created successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa độ ưu tiên", description = "Thay đổi độ ưu tiên cho task")
    @PatchMapping("/{idProject}/tasks/{idTask}/update-priority")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updatePriority(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskUpdatePriorityRequestDTO requestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.updatePriority(idTask, requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has been updated priority successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Chỉnh sửa mối quan hệ của task", description = "Thay đổi mối quan hệ của task với task khác")
    @PatchMapping("/{idProject}/tasks/{idTask}/update-relationship")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateRelationship(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskUpdateRelationshipRequestDTO requestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.updateRelationship(idTask, requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has been updated relationship successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa trạng thái của task", description = "Thay đổi trạng thái của task")
    @PatchMapping("/{idProject}/tasks/{idTask}/update-status")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateStatus(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskUpdateStatusRequestDTO requestDTO, @AuthenticationPrincipal Account account) {

        TaskResponseDTO taskResponseDTO = taskService.updateStatus(idTask, requestDTO, account);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has been updated status successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Phân công task task", description = "Phân công task cho account trong nhóm member của project")
    @PatchMapping("/{idProject}/tasks/{idTask}/assign")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> assignAccount(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskAssignRequestDTO requestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.assignTask(idTask, idProject, requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has been assigned successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chuyển section cho task", description = "Thay đổi section cho task")
    @PatchMapping("/{idProject}/tasks/{idTask}/update-section")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateSection(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskUpdateSectionRequestDTO requestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.updateSectionForTask(idTask, idProject, requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has been updated section successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }

}
