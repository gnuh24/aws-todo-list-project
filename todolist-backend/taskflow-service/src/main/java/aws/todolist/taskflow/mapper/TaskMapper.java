package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.event.ActorDto;
import aws.todolist.taskflow.dto.event.dto.TaskEventDto;
import aws.todolist.taskflow.dto.event.payload.TaskPayload;
import aws.todolist.taskflow.dto.task.TaskDetailResponseDTO;
import aws.todolist.taskflow.dto.task.TaskResponseDTO;
import aws.todolist.taskflow.entity.Task;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Map Task → TaskResponseDTO (task cha)
    @Mapping(target = "idTaskCha", source = "taskFather.id")
    @Mapping(target = "idAccountCreate", source = "createdByAccount.id")
    @Mapping(target = "idAccountAssigned", source = "accountAssign.id")
    @Mapping(target = "idSection", source = "section.id")
    @Mapping(target = "idProject", source = "section.project.id")
    @Mapping(target = "sectionName", source = "section.name")
    @Mapping(target = "projectName", source = "section.project.name")
    TaskResponseDTO toResponse(Task task);

    // Map list Task → list TaskResponseDTO (task cha, filter isDeleted + null taskFather)
    default List<TaskResponseDTO> toResponseList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return List.of();
        return tasks.stream()
                .filter(task -> !task.getIsDeleted() && task.getTaskFather() == null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Map list Task con → list TaskResponseDTO (taskChild)
    @Named("taskChildList")
    default List<TaskResponseDTO> toResponseListTaskChild(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return List.of();
        return tasks.stream()
                .filter(task -> !task.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Map Task → TaskDetailResponseDTO
    @Mapping(target = "taskChild", source = "taskChild", qualifiedByName = "taskChildList")
    @Mapping(target = "idSection", source = "section.id")
    @Mapping(target = "idProject", source = "section.project.id")
    @Mapping(target = "sectionName", source = "section.name")
    @Mapping(target = "projectName", source = "section.project.name")
    TaskDetailResponseDTO toDetailResponse(Task task);

    // Sau khi mapping, lọc comment bị xóa và map commentAttach
    @AfterMapping
    default void handleAfterMapping(Task task, @MappingTarget TaskDetailResponseDTO dto) {
        if (task.getTaskComments() != null) {
            TaskCommentMapper taskCommentMapper = Mappers.getMapper(TaskCommentMapper.class);
            dto.setComments(taskCommentMapper.toResponseListFiltered(task.getTaskComments()));
        }
        if (task.getTaskLabels() != null) {
            TaskLabelMapper taskLabelMapper = Mappers.getMapper(TaskLabelMapper.class);
            dto.setLabels(taskLabelMapper.toResponseList(task.getTaskLabels()));
        }
    }

    @Mapping(target = "idTaskCha", source = "taskFather.id")
    @Mapping(target = "idAccountCreate", source = "createdByAccount.id")
    @Mapping(target = "idAccountAssigned", source = "accountAssign.id")
    @Mapping(target = "idSection", source = "section.id")
    @Mapping(target = "idProject", source = "section.project.id")
    TaskEventDto toEventDto(Task task);

    default TaskPayload toPayload(
            Task task,
            ActorDto actor,
            List<String> receivers
    ) {
        TaskPayload payload = new TaskPayload();

        payload.setProjectId(
                task.getSection().getProject().getId()
        );

        payload.setActor(actor);
        payload.setReceivers(receivers);
        payload.setTask(toEventDto(task));

        return payload;
    }


}
