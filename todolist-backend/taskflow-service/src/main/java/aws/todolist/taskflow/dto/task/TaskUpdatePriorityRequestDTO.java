package aws.todolist.taskflow.dto.task;


import aws.todolist.taskflow.enums.Priority;
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
@Schema(description = "Request DTO để cập nhật độ ưu tiên cho task")
public class TaskUpdatePriorityRequestDTO {

    @NotNull(message = "Priority không được để trống")
    @Schema(description = "Mức độ ưu tiên mới của Task", example = "HIGH")
    private Priority priority;
}
