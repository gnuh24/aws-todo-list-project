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
public class SectionCreateRequestDTO {

    @NotBlank(message = "Không được để trống tên section")
    private String name;
}
