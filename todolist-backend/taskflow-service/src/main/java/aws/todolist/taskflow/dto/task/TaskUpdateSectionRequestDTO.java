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
@Schema(description = "Request DTO để cập nhật section cho task")
public class TaskUpdateSectionRequestDTO {

    @NotBlank(message = "Không được để trống id task")
    private String idSection;
}
