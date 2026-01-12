package aws.todolist.taskflow.controller;

import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRoleRequestDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateStatusRequestDTO;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.service.ServiceInterface.AccountService;
import aws.todolist.taskflow.service.ServiceInterface.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Member API", description = "CRUD của member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Lấy danh sách member của project", description = "Dùng id client cung cấp để lấy danh sách member")
    @GetMapping("/{idProject}/members")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> getMemberByIdProject(@PathVariable("idProject") String id) {
        List<MemberResponseDTO> members = memberService.getAllMember(id);

        ApiResponse<List<MemberResponseDTO>> response = new ApiResponse<>(200, "Danh sách thành viên đã lấy thành công", members);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thêm thành viên mới vào dự án", description = "Thêm một member mới vào dự án")
    @PostMapping("/{idProject}/members")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<MemberResponseDTO>> addNewMember(
            @PathVariable("idProject") String projectId,
            @RequestBody @Valid MemberCreateRequestDTO request) {

        MemberResponseDTO memberResponseDTO = memberService.addNewMember(projectId, request);

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(
                200,
                "Thành viên đã được thêm vào dự án thành công",
                memberResponseDTO
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thay đổi vai trò", description = "Thay đổi vai trò của member")
    @PatchMapping("/{idProject}/members/{idMember}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMember(
            @PathVariable("idProject") String id,
            @PathVariable("idMember") String idMember,
            @RequestBody @Valid MemberUpdateRoleRequestDTO request) {

        MemberResponseDTO memberResponseDTO = memberService.updateRoleMember(idMember, request);

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(
                200,
                "Vai trò của thành viên đã được cập nhật thành công",
                memberResponseDTO
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật trạng thái member", description = "Chuyển trạng thái cho member")
    @PatchMapping("/{idProject}/members/response")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateStatusMember(
            @PathVariable("idProject") String id,
            @RequestBody @Valid MemberUpdateStatusRequestDTO request
    ) {


        MemberResponseDTO memberResponseDTO = memberService.responseRequestMember(id, request);

        String message;
        if (memberResponseDTO.getStatus() == StatusMember.ACCEPTED) {
            message = "Bạn đã chấp nhận lời mời tham gia dự án.";
        } else {
            message = "Bạn đã từ chối lời mời tham gia dự án.";
        }

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(200, message, memberResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa member", description = "Cập nhật member về trạng thái đã xóa")
    @DeleteMapping("/{idProject}/members/{idMember}")
    @RequireProjectRole({Role.OWNER})
    public ResponseEntity<ApiResponse<MemberResponseDTO>> deleteMember(
            @PathVariable("idProject") String id,
            @PathVariable("idMember") String idMember) {

        MemberResponseDTO memberResponseDTO = memberService.deleteMember(idMember);

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(
                200,
                "Thành viên đã được xóa khỏi dự án thành công",
                memberResponseDTO
        );

        return ResponseEntity.ok(response);
    }

}
