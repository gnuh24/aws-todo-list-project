package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.taskLabel.TaskLabelResponseDTO;
import aws.todolist.taskflow.entity.TaskLabel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskLabelMapper {

    // Map TaskLabel → TaskLabelResponseDTO
    @Mapping(target = "name", source = "projectLabel.name")
    TaskLabelResponseDTO toResponse(TaskLabel taskLabel);

    // Map list
    List<TaskLabelResponseDTO> toResponseList(List<TaskLabel> taskLabels);
}
