package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = SectionMapper.class)
public interface ProjectMapper {

    // Map Project → ProjectResponseDTO (chỉ id+name+section list name/id)
    @Mapping(target = "section", source = "sections", qualifiedByName = "nameAndId")
    ProjectResponseDTO toResponse(Project project);

    // Map list Project → list ProjectResponseDTO
    List<ProjectResponseDTO> toResponseList(List<Project> projects);

    // Map Project → ProjectDetailResponseDTO (full, sections full)
    @Mapping(target = "sections", source = "sections", qualifiedByName = "full")
    ProjectDetailResponseDTO toDetailResponse(Project project);
}
