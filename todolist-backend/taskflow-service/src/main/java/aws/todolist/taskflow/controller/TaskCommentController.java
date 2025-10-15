package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.TaskCommentService;
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
// TODO: FULL quyền
public class TaskCommentController {

    @Autowired
    private TaskCommentService taskCommentService;

    @Operation(summary = "Tạo comment mới", description = "Tạo thêm một comment mới")
    @PostMapping("/{idProject}/tasks/{idTask}/comments")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskCommentResponseDTO>> addNewComment(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskCommentRequestDTO requestDTO, @AuthenticationPrincipal Account account) {

        TaskCommentResponseDTO taskComment = taskCommentService.addNewComment(requestDTO, idTask, account);

        ApiResponse<TaskCommentResponseDTO> response = new ApiResponse<>(200, "Comment has been created successfully", taskComment);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa comment", description = "Thay đổi nội dung comment đã tạo")
    @PatchMapping("/{idProject}/tasks/comments/{idComment}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskCommentResponseDTO>> updateComment(@PathVariable("idProject") String idProject, @PathVariable("idComment") String idComment, @RequestBody @Valid TaskCommentRequestDTO requestDTO, @AuthenticationPrincipal Account account) {

        TaskCommentResponseDTO taskComment = taskCommentService.updateComment(requestDTO, idComment, account);

        ApiResponse<TaskCommentResponseDTO> response = new ApiResponse<>(200, "Comment has been updated successfully", taskComment);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Xóa comment", description = "Cập nhật trạng thái comment là đã xóa")
    @DeleteMapping("/{idProject}/tasks/comments/{idComment}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskCommentResponseDTO>> deleteComment(@PathVariable("idProject") String idProject, @PathVariable("idComment") String idComment, @AuthenticationPrincipal Account account) {

        TaskCommentResponseDTO taskComment = taskCommentService.deleteComment(idComment, account);

        ApiResponse<TaskCommentResponseDTO> response = new ApiResponse<>(200, "Comment has been deleted successfully", taskComment);

        return ResponseEntity.ok(response);
    }


}
