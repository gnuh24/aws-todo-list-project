package aws.todolist.project.dto.project;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUpdateRequestDTO {

    private String name;

    private Boolean isArchived; // default false
}
