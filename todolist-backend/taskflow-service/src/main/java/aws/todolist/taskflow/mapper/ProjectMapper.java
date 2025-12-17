package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.event.ActorDto;
import aws.todolist.taskflow.dto.event.dto.ProjectEventDto;
import aws.todolist.taskflow.dto.event.payload.ProjectPayload;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = SectionMapper.class)
public interface ProjectMapper {

    // Mapper để gửi kafka event
    // Map từ entity Project sang DTO
    ProjectEventDto toDto(Project project);

    // Map từ entity sang ProjectPayload
    default ProjectPayload toPayload(
            Project project,
            ActorDto actor,
            List<String> receivers
    ) {
        ProjectPayload payload = new ProjectPayload();

        payload.setProjectId(project.getId());
        payload.setActor(actor);
        payload.setReceivers(receivers);
        payload.setNotification(null); // project event thường không notify

        payload.setProject(toDto(project));

        return payload;
    }

    // Mapper cho api response

    // Map Project → ProjectResponseDTO (chỉ id+name+section list name/id)
    @Mapping(target = "section", source = "sections", qualifiedByName = "nameAndId")
    ProjectResponseDTO toResponse(Project project);

    // Map list Project → list ProjectResponseDTO
    List<ProjectResponseDTO> toResponseList(List<Project> projects);

    // Map Project → ProjectDetailResponseDTO (full, sections full)
    @Mapping(target = "sections", source = "sections", qualifiedByName = "full")
    ProjectDetailResponseDTO toDetailResponse(Project project);
}
