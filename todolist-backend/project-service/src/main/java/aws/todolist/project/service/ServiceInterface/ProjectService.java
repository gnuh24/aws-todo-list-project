package aws.todolist.project.service.ServiceInterface;


import aws.todolist.project.dto.project.ProjectCreateRequestDTO;
import aws.todolist.project.dto.project.ProjectDetailResponseDTO;
import aws.todolist.project.dto.project.ProjectResponseDTO;
import aws.todolist.project.dto.project.ProjectUpdateRequestDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectResponseDTO> getAllProject();

    ProjectDetailResponseDTO getProjectById(String id);

    ProjectResponseDTO addProject(ProjectCreateRequestDTO projectCreateRequestDTO);

    ProjectResponseDTO updateProject(String id, ProjectUpdateRequestDTO projectUpdateRequestDTO);

    ProjectResponseDTO removeProject(String id);
}
