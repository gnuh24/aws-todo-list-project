package aws.todolist.taskflow.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequestDTO {

    @NotBlank(message = "Tên dự án không được để trống")
    private String name;

    private Boolean isArchived = false; // default false

    private Boolean isDefault = false;
}