package aws.todolist.taskflow.dto.section;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionUpdateNameDTO {

    @NotBlank(message = "Tên của section không để trống")
    private String name;
}
