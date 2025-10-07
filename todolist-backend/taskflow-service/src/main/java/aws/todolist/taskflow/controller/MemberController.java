package aws.todolist.taskflow.controller;

import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRequestDTO;
import aws.todolist.taskflow.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Project API", description = "CRUD của member")
// TODO: ADMIN, OWNER
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Operation(summary = "Lấy danh sách member của project", description = "Dùng id client cung cấp để lấy danh sách member")
    @GetMapping("/{idProject}/members")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> getMemberByIdProject(@PathVariable("idProject") String id) {
        List<MemberResponseDTO> members = memberService.getAllMember(id);

        ApiResponse<List<MemberResponseDTO>> response = new ApiResponse<>(200, "list members has getted successfully", members);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thêm thành viên mới vào dự án", description = "Thêm một member mới vào dự án")
    @PostMapping("/{idProject}/members")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> addNewMember(@PathVariable("idProject") String id, @RequestBody @Valid MemberCreateRequestDTO request) {

        MemberResponseDTO memberResponseDTO = memberService.addNewMember(id, request);

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(200, "member has added to this project successfully", memberResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thay đổi vai trò", description = "Thay đổi vai trò của member")
    @PatchMapping("/members/{idMember}")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMember(@PathVariable("idMember") String idMember, @RequestBody @Valid MemberUpdateRequestDTO request) {

        MemberResponseDTO memberResponseDTO = memberService.updateRoleMember(idMember, request);

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(200, "member has changed role successfully", memberResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa member", description = "Cập nhật member về trạng thái đã xóa")
    @DeleteMapping("/members/{idMember}")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> deleteMember(@PathVariable("idMember") String idMember) {

        MemberResponseDTO memberResponseDTO = memberService.deleteMember(idMember);

        ApiResponse<MemberResponseDTO> response = new ApiResponse<>(200, "member was deleted successfully", memberResponseDTO);

        return ResponseEntity.ok(response);
    }
}
