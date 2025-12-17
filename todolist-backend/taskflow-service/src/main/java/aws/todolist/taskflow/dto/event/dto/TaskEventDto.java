package aws.todolist.taskflow.dto.event.dto;

import aws.todolist.taskflow.enums.Priority;
import aws.todolist.taskflow.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEventDto {

    private String id;
    private String title;

    private Status status;
    private Priority priority;

    private Boolean isPinned;
    private LocalDateTime deadline;
    private LocalDateTime startTime;

    private String sectionId;
    private String assigneeId;
}
