package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.task.TaskDetailResponseDTO;
import aws.todolist.taskflow.dto.task.TaskResponseDTO;
import aws.todolist.taskflow.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    @Autowired
    private TaskCommentMapper taskCommentMapper;
    
    @Autowired
    private TaskLabelMapper taskLabelMapper;

    public TaskResponseDTO ResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isPinned(task.getIsPinned())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .startTime(task.getStartTime())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .idTaskCha(task.getTaskFather() != null ? task.getTaskFather().getId() : null)
                .idAccountCreate(task.getCreatedByAccount().getId())
                .idAccountAssigned(task.getAccountAssign() != null ? task.getAccountAssign().getId() : null)
                .idSection(task.getSection().getId())
                .idProject(task.getSection().getProject().getId())
                .build();
    }

    public TaskDetailResponseDTO ResponseDetailDTO(Task task) {
        if (task == null) return null;

        return TaskDetailResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isArchived(task.getIsArchived())
                .isPinned(task.getIsPinned())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .startTime(task.getStartTime())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .taskChild(this.ResponseDTOListTaskChild(task.getTaskChild()))
                .comments(taskCommentMapper.toResponseList(task.getTaskComments()))
                .idAccountAssigned(task.getAccountAssign() != null ? task.getAccountAssign().getId() : null)
                .idAccountCreate(task.getCreatedByAccount().getId())
                .idSection(task.getSection().getId())
                .idProject(task.getSection().getProject().getId())
	    .labels(taskLabelMapper.toResponseList(task.getTaskLabels()))
                .build();
    }

    public List<TaskResponseDTO> ResponseDTOList(List<Task> tasks) {
        return tasks.stream()
                .filter(task ->
                        !task.getIsDeleted() && task.getTaskFather() == null
                )
                .map(this::ResponseDTO)
                .toList();
    }


    // Chuyển danh sách task con thành danh sách DTO
    public List<TaskResponseDTO> ResponseDTOListTaskChild(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> !task.getIsDeleted())
                .map(this::ResponseDTO)
                .toList();
    }
}
