package aws.todolist.taskflow.service.ServiceInterface;


import aws.todolist.taskflow.dto.project.ProjectCreateRequestDTO;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectUpdateRequestDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectResponseDTO> getAllProject();

    ProjectDetailResponseDTO getProjectById(String id);

    ProjectResponseDTO addProject(ProjectCreateRequestDTO projectCreateRequestDTO);

    ProjectResponseDTO updateProject(String id, ProjectUpdateRequestDTO projectUpdateRequestDTO);

    ProjectResponseDTO removeProject(String id);

    void addProjectDefault(String idAccount);

    ProjectDetailResponseDTO getProjectDefault();
}
