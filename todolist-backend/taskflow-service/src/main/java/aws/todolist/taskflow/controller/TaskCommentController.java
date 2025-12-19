package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.ServiceInterface.AccountService;
import aws.todolist.taskflow.service.ServiceInterface.TaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Task Comment API", description = "CRUD của task comment")
// TODO: FULL quyền
public class TaskCommentController {

    @Autowired
    private TaskCommentService taskCommentService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Tạo comment mới", description = "Tạo thêm một comment mới")
    @PostMapping("/{idProject}/tasks/{idTask}/comments")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskCommentResponseDTO>> addNewComment(@PathVariable("idProject") String idProject, @PathVariable("idTask") String idTask, @RequestBody @Valid TaskCommentRequestDTO requestDTO) {

        TaskCommentResponseDTO taskComment = taskCommentService.addNewComment(requestDTO, idTask);

        ApiResponse<TaskCommentResponseDTO> response = new ApiResponse<>(200, "Comment đã được tạo thành công", taskComment);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa comment", description = "Thay đổi nội dung comment đã tạo")
    @PatchMapping("/{idProject}/tasks/comments/{idComment}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskCommentResponseDTO>> updateComment(@PathVariable("idProject") String idProject, @PathVariable("idComment") String idComment, @RequestBody @Valid TaskCommentRequestDTO requestDTO) {

        TaskCommentResponseDTO taskComment = taskCommentService.updateComment(requestDTO, idComment);

        ApiResponse<TaskCommentResponseDTO> response = new ApiResponse<>(200, "Comment đã được cập nhật thành công", taskComment);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa comment", description = "Cập nhật trạng thái comment là đã xóa")
    @DeleteMapping("/{idProject}/tasks/comments/{idComment}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<TaskCommentResponseDTO>> deleteComment(@PathVariable("idProject") String idProject, @PathVariable("idComment") String idComment) {
        TaskCommentResponseDTO taskComment = taskCommentService.deleteComment(idComment);

        ApiResponse<TaskCommentResponseDTO> response = new ApiResponse<>(200, "Comment đã được xóa thành công", taskComment);

        return ResponseEntity.ok(response);
    }


}
