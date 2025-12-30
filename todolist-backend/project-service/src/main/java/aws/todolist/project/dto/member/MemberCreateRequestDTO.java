package aws.todolist.project.dto.member;


import aws.todolist.project.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateRequestDTO {
    @NotBlank(message = "Không để trống id của account")
    private String idAccount;

    @NotNull(message = "Không để trống vai trò")
    private Role role;
}
