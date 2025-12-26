package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.ServiceInterface.CommentAttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Comment Attach API", description = "CRUD của comment attach")
public class CommentAttachController {

    @Autowired
    private CommentAttachService commentAttachService;

    @Operation(summary = "Xóa comment attach", description = "Xóa comment attach")
    @DeleteMapping("/{idProject}/deleteCommentAttach")
    @RequireProjectRole({Role.OWNER, Role.MEMBER, Role.ADMIN, Role.VIEWER})
    public ResponseEntity<ApiResponse<CommentAttachResponse>> addNewComment(@PathVariable("idProject") String idProject, @RequestParam("fileUrl") String fileUrl) {

        CommentAttachResponse commentAttachResponse = commentAttachService.deleteCommentAttach(fileUrl);

        ApiResponse<CommentAttachResponse> response = new ApiResponse<>(200, "Comment Attach đã được xóa thành công", commentAttachResponse);

        return ResponseEntity.ok(response);
    }

}
