package aws.todolist.taskflow.dto.member;

import aws.todolist.taskflow.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateRequestDTO {
    @NotNull(message = "Không để trống vai trò")
    private Role role;
}

