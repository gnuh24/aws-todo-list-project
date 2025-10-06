package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {

    @Autowired
    private SectionMapper sectionMapper;

    public ProjectResponseDTO ResponseDTO(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .isArchived(project.getIsArchived())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    public List<ProjectResponseDTO> ResponseDTOList(List<Project> projects) {
        return projects.stream()
                .map(this::ResponseDTO)
                .toList();
    }

    public ProjectDetailResponseDTO ResponseDTODetail(Project project) {
        return ProjectDetailResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .isArchived(project.getIsArchived())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .sections(sectionMapper.ResponseDTOList(project.getSections()))
                .build();

    }

}
