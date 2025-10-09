package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.task.TaskCreateRequestDTO;
import aws.todolist.taskflow.dto.task.TaskDetailResponseDTO;
import aws.todolist.taskflow.dto.task.TaskResponseDTO;
import aws.todolist.taskflow.dto.task.TaskUpdatePriorityRequestDTO;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        TaskResponseDTO taskResponseDTO = taskService.addTask(requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has fetched successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa độ ưu tiên", description = "Thay đổi độ ưu tiên cho task")
    @PatchMapping("/{idProject}/tasks/{idTask}/update-priority")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updatePriority(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskUpdatePriorityRequestDTO requestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.updatePriority(idTask, requestDTO);

        ApiResponse<TaskResponseDTO> response = new ApiResponse<>(200, "Task has fetched successfully", taskResponseDTO);

        return ResponseEntity.ok(response);
    }
}
