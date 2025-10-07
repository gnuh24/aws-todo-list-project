package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.project.ProjectCreateRequestDTO;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectUpdateRequestDTO;
import aws.todolist.taskflow.entity.*;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.mapper.ProjectMapper;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import aws.todolist.taskflow.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Override
    public List<ProjectResponseDTO> getAllProject(String accountID) {
        List<Project> projects = projectRepository.findAllByAccountId(accountID);

        return projectMapper.ResponseDTOList(projects);

    }

    @Override
    public ProjectDetailResponseDTO getProjectById(String id) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new BadRequestException("Project không tồn tại hoặc đã bị xóa");
        }

        return projectMapper.ResponseDTODetail(project);
    }


    @Transactional
    @Override
    public ProjectResponseDTO addProject(ProjectCreateRequestDTO projectCreateRequestDTO, Account account) {

        Project project = Project.builder()
                .name(projectCreateRequestDTO.getName())
                .isArchived(projectCreateRequestDTO.getIsArchived() != null ? projectCreateRequestDTO.getIsArchived() : false)
                .build();

        Project saved = projectRepository.saveAndFlush(project);

        Member member = Member.builder().account(account).project(saved).role(Role.OWNER).build();

        Section section = Section.builder().project(saved).name("Section default").position(1).build();

        memberRepository.save(member);

        sectionRepository.save(section);

        saved.getMembers().add(member);

        saved.getSections().add(section);

        return projectMapper.ResponseDTO(saved);
    }

    @Transactional
    @Override
    public ProjectResponseDTO updateProject(String id, ProjectUpdateRequestDTO projectUpdateRequestDTO) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new BadRequestException("Project không tồn tại hoặc đã bị xóa");
        }

        if (projectUpdateRequestDTO.getName() != null) {
            project.setName(projectUpdateRequestDTO.getName());
        }

        if (projectUpdateRequestDTO.getIsArchived() != null) {
            project.setIsArchived(projectUpdateRequestDTO.getIsArchived());
        }

        Project saved = projectRepository.saveAndFlush(project);

        return projectMapper.ResponseDTO(saved);
    }

    @Transactional
    @Override
    public ProjectResponseDTO removeProject(String id) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new BadRequestException("Project không tồn tại hoặc đã bị xóa");
        }

        // Chạy vòng lặp để cập nhật các section, task, taskcomment của project về trạng thái deleted
        project.getSections().forEach(section -> {
            section.softDelete();
            section.getTasks().forEach(task -> {
                task.softDelete();
                task.getTaskComments().forEach(TaskComment::softDelete);
            });
        });

        // Cập nhật các member về deleted
        project.getMembers().forEach(Member::softDelete);

        project.softDelete();

        Project saved = projectRepository.saveAndFlush(project);
        return projectMapper.ResponseDTO(saved);
    }

    @Transactional
    @Override
    public ProjectResponseDTO restoreProject(String id) {

        Optional<Project> optProject = projectRepository.findById(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new BadRequestException("Project không tồn tại");
        }

        project.getSections().forEach(section -> {
            section.restore();
            section.getTasks().forEach(task -> {
                task.restore();
                task.getTaskComments().forEach(TaskComment::restore);
            });
        });

        project.restore();

        Project saved = projectRepository.saveAndFlush(project);
        return projectMapper.ResponseDTO(saved);

    }
}
