package aws.todolist.taskflow.dto.task;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO để cập nhật mối quan hệ task cha cho task")
public class TaskUpdateRelationshipRequestDTO {

    @Size(min = 1, message = "Title phải có ít nhất 1 ký tự nếu không null")
    private String idTaskFather;
}
