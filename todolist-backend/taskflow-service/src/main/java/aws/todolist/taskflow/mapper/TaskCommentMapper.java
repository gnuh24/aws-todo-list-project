package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.TaskComment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommentAttachMapper.class)
public interface TaskCommentMapper {


    // Map entity → DTO, nested mapping
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "authorName", source = "account.displayName")
    @Mapping(target = "authorAvatar", source = "account.avatar")
    TaskCommentResponseDTO toResponse(TaskComment comment);

    // Sau khi mapping, lọc comment bị xóa và map commentAttach
    @AfterMapping
    default void handleAfterMapping(TaskComment comment, @MappingTarget TaskCommentResponseDTO dto) {
        if (comment.getCommentAttaches() != null) {
            CommentAttachMapper mapper = Mappers.getMapper(CommentAttachMapper.class);
            dto.setCommentAttach(mapper.toResponseList(comment.getCommentAttaches()));
        }
    }

    // Nếu muốn lọc comment bị xóa khi map list
    default List<TaskCommentResponseDTO> toResponseListFiltered(List<TaskComment> comments) {
        if (comments == null || comments.isEmpty()) return List.of();
        return comments.stream()
                .filter(tc -> tc.getIsDeleted() == null || !tc.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
