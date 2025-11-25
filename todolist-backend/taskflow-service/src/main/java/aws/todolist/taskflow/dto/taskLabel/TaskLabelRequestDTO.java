package aws.todolist.taskflow.dto.taskLabel;

import lombok.Data;

@Data
public class TaskLabelRequestDTO {
    private String projectLabelId;
    private String personalLabelId;
}
