package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.mapper.ProjectMapper;
import aws.todolist.taskflow.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<ProjectResponseDTO> getAllProject(String accountID) {
        List<Project> projects = projectRepository.findAllByAccountId(accountID);

        return projectMapper.ResponseDTOList(projects);

    }

    @Override
    public ProjectDetailResponseDTO getProjectById(String id) {

        Project project = projectRepository.getReferenceById(id);

        return projectMapper.ResponseDTODetail(project);
    }

    @Override
    public Project addProject(Project project) {
        return null;
    }

    @Override
    public Project updateProject(String id, Project updateProject) {
        return null;
    }

    @Override
    public Project removeProject(String id) {
        return null;
    }
}
