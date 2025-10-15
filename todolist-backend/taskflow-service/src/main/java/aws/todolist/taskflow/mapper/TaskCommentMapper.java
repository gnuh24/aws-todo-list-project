package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.TaskComment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskCommentMapper {

    public TaskCommentResponseDTO toResponse(TaskComment comment) {
        if (comment == null) return null;

        return TaskCommentResponseDTO.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .accountId(comment.getAccount().getId())
                .authorName(comment.getAccount().getUsername())
                .authorAvatar(comment.getAccount().getAvatar())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /**
     * Chuyển danh sách TaskComment entity sang danh sách DTO
     */
    public List<TaskCommentResponseDTO> toResponseList(List<TaskComment> comments) {
        if (comments == null || comments.isEmpty()) return List.of();

        return comments.stream()
                .filter(taskComment -> !taskComment.getIsDeleted())
                .map(this::toResponse)
                .toList();
    }

}
