package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.task.TaskResponseDTO;
import aws.todolist.taskflow.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    public TaskResponseDTO ResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .isPinned(task.getIsPinned())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .taskChild(this.ResponseDTOListTaskChild(task.getTaskChild()))
                .build();
    }

    public List<TaskResponseDTO> ResponseDTOList(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> !task.getIsDeleted() && task.getTaskFather() == null)
                .map(this::ResponseDTO)
                .toList();
    }

    public List<TaskResponseDTO> ResponseDTOListTaskChild(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> !task.getIsDeleted())
                .map(this::ResponseDTO)
                .toList();
    }
}
