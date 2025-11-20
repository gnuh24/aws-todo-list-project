package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.entity.CommentAttach;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentAttachMapper {
    /**
     * Chuyển đổi từ CommentAttach entity sang DTO
     */
    public CommentAttachResponse toResponse(CommentAttach entity) {
        if (entity == null) return null;

        CommentAttachResponse dto = new CommentAttachResponse();
        dto.setId(entity.getId());
        dto.setAttachmentUrl(entity.getAttachmentUrl());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getTaskComment() != null) {
            dto.setTaskCommentId(entity.getTaskComment().getId());
        }

        return dto;
    }

    /**
     * Chuyển đổi danh sách CommentAttach entity sang danh sách DTO
     */
    public List<CommentAttachResponse> toResponseList(List<CommentAttach> entities) {
        if (entities == null || entities.isEmpty()) return List.of();

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
