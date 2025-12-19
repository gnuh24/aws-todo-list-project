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

    // ===== Core =====
    private String id;
    private String title;
    private String description;

    private Boolean isPinned;
    private Status status;
    private Priority priority;

    // ===== Time =====
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== Relation =====
    private String idTaskCha;
    private String idAccountCreate;
    private String idAccountAssigned;
    private String idSection;
    private String idProject;
}