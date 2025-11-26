package aws.todolist.taskflow.dto.personalLabel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalLabelResponseDTO {
    private String id;
    private String name;
}
