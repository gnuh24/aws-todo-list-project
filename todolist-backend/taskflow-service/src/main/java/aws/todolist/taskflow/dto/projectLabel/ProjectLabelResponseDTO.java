package aws.todolist.taskflow.dto.projectLabel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectLabelResponseDTO {
	private String id;
	private String name;
}
