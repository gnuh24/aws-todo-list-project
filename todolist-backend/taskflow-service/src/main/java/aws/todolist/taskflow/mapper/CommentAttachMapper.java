package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.entity.CommentAttach;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentAttachMapper {

    // Map entity → DTO
    @Mapping(target = "taskCommentId", source = "taskComment.id")
    CommentAttachResponse toResponse(CommentAttach entity);

    // Map danh sách
    List<CommentAttachResponse> toResponseList(List<CommentAttach> entities);

}
