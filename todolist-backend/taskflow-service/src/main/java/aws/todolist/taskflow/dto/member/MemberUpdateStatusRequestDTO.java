package aws.todolist.taskflow.dto.member;

import aws.todolist.taskflow.enums.StatusMember;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO để cập nhật trạng thái cho member")
public class MemberUpdateStatusRequestDTO {

    @NotNull(message = "Status không được để trống")
    @Schema(description = "Trạng thái của member", example = "ACCEPTED")
    private StatusMember status;
}
