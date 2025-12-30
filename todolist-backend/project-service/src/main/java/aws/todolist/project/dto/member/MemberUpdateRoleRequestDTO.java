package aws.todolist.project.dto.member;

import aws.todolist.project.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateRoleRequestDTO {
    @NotNull(message = "Không để trống vai trò")
    private Role role;
}

