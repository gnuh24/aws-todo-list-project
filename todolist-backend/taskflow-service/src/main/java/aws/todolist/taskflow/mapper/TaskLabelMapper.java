package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.taskLabel.TaskLabelResponseDTO;
import aws.todolist.taskflow.entity.TaskLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskLabelMapper {
	
	
	public TaskLabelResponseDTO toResponse(TaskLabel comment) {
		if (comment == null) return null;
		
		return TaskLabelResponseDTO.builder()
		    .id(comment.getId())
		    .confidence(comment.getConfidence())
		    .isAiGenerated(comment.getIsAiGenerated())
		    .name(comment.getProjectLabel().getName())
		    .build();
	}
	
	/**
	 * Chuyển danh sách TaskLabel entity sang danh sách DTO
	 */
	public List<TaskLabelResponseDTO> toResponseList(List<TaskLabel> comments) {
		if (comments == null || comments.isEmpty()) return List.of();
		
		return comments.stream()
		    .map(this::toResponse)
		    .toList();
	}
	
	
}
