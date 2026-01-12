package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.ServiceInterface.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1")
@Tag(name = "Invite API", description = "Tạo và xác thực link mời vào project")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    // ===============================
    // OWNER tạo link mời
    // ===============================
    @Operation(
            summary = "Tạo link mời vào project",
            description = "Chỉ OWNER mới có quyền tạo link mời"
    )
    @PostMapping("/projects/{idProject}/invites")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<Map<String, String>>> createInviteLink(
            @PathVariable("idProject") String projectId) {

        String inviteUrl = inviteService.createInviteLink(projectId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Tạo link mời thành công",
                        Map.of("inviteUrl", inviteUrl)
                )
        );
    }

    // ===============================
    // User verify link mời (phải login)
    // ===============================
    @Operation(
            summary = "Xác thực link mời",
            description = "Người dùng đăng nhập để xác nhận lời mời"
    )
    @PostMapping("/invites/verify")
    public ResponseEntity<ApiResponse<Void>> verifyInvite(
            @RequestParam String token
    ) {

        inviteService.verifyInvite(token);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Lời mời đã được tạo. Vui lòng phản hồi lời mời.",
                        null
                )
        );
    }
}