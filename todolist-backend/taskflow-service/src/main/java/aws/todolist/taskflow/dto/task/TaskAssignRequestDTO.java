package aws.todolist.taskflow.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO để thực hiện phân công cho task")
public class TaskAssignRequestDTO {

    @NotBlank(message = "Không được để trống id của account")
    private String idAccount;
}
