package aws.todolist.taskflow.dto.task;


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
@Schema(description = "Request DTO để cập nhật archived cho task")
public class TaskArchivedRequestDTO {

    @NotNull(message = "Trạng thái isArchived không được phép null")
    private Boolean isArchived;
}
