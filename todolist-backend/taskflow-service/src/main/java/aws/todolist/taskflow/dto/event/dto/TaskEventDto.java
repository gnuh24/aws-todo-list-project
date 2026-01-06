package aws.todolist.taskflow.dto.event.dto;

import aws.todolist.taskflow.dto.member.MemberDTO;
import aws.todolist.taskflow.dto.taskLabel.TaskLabelResponseDTO;
import aws.todolist.taskflow.enums.Priority;
import aws.todolist.taskflow.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEventDto {

    // ===== Core =====
    private String id;
    private String title;
    private String description;

    private Boolean isArchived;
    private Boolean isPinned;

    private Status status;
    private Priority priority;

    // ===== Time =====
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== Relation (Object) =====
    private MemberDTO createdByAccount;
    private MemberDTO accountAssign;

    // ===== Relation (ID) =====
    private String idTaskCha;
    private String idSection;
    private String idProject;

    // ===== Extra for FE =====
    private String sectionName;
    private String projectName;

    // ===== Label (light) =====
    private List<TaskLabelResponseDTO> labels;
}
