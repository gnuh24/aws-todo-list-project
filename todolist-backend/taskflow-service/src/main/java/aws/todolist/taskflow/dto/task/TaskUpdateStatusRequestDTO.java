package aws.todolist.taskflow.dto.task;


import aws.todolist.taskflow.enums.Status;
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
@Schema(description = "Request DTO để cập nhật trạng thái cho task")
public class TaskUpdateStatusRequestDTO {

    @NotNull(message = "Không được để trạng thái là null")
    private Status status;
}
