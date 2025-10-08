package aws.todolist.taskflow.service;


import aws.todolist.taskflow.dto.project.ProjectCreateRequestDTO;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectUpdateRequestDTO;
import aws.todolist.taskflow.entity.Account;

import java.util.List;

public interface ProjectService {

    List<ProjectResponseDTO> getAllProject(String AccountID);

    ProjectDetailResponseDTO getProjectById(String id);

    ProjectResponseDTO addProject(ProjectCreateRequestDTO projectCreateRequestDTO, Account account);

    ProjectResponseDTO updateProject(String id, ProjectUpdateRequestDTO projectUpdateRequestDTO);

    ProjectResponseDTO removeProject(String id, Account account);

    ProjectResponseDTO restoreProject(String id);
}
