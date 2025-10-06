package aws.todolist.taskflow.service;


import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.entity.Project;

import java.util.List;

public interface ProjectService {

    List<ProjectResponseDTO> getAllProject(String AccountID);

    ProjectDetailResponseDTO getProjectById(String id);

    Project addProject(Project project);

    Project updateProject(String id, Project updateProject);

    Project removeProject(String id);
}
