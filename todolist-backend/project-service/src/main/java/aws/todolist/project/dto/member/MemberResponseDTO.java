package aws.todolist.project.dto.member;

import aws.todolist.project.enums.Role;
import aws.todolist.project.enums.StatusMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {

    @Schema(description = "Mã ID của member", example = "uuid-member")
    private String id;

    @Schema(description = "Mã ID account của member", example = "uuid-account")
    private String accountId;

    @Schema(description = "Tên hiển thị của member", example = "Nguyen Van A")
    private String displayName;

    @Schema(description = "URL avatar của member", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "email", example = "abv@gmail.com")
    private String email;

    @Schema(description = "Role của member trong project", example = "OWNER")
    private Role role;

    @Schema(description = "Trạng thái của member trong project", example = "OWNER")
    private StatusMember status;

    @Schema(description = "Ngày tạo member", example = "2025-10-06T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Ngày cập nhật member", example = "2025-10-06T11:00:00")
    private LocalDateTime updatedAt;
}
