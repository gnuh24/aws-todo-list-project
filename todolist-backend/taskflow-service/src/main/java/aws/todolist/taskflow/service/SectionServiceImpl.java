package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.section.SectionCreateRequestDTO;
import aws.todolist.taskflow.dto.section.SectionDeleteAndMigrateDTO;
import aws.todolist.taskflow.dto.section.SectionResponseDTO;
import aws.todolist.taskflow.dto.section.SectionUpdateRequestDTO;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.entity.Section;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.SectionMapper;
import aws.todolist.taskflow.repository.ProjectRepository;
import aws.todolist.taskflow.repository.SectionRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @Override
    public List<SectionResponseDTO> getAllSection(String idProject) {

        List<Section> sections = sectionRepository.findByProjectIdAndIsDeletedFalseOrderByPositionAsc(idProject);

        return sectionMapper.ResponseDTOList(sections);
    }

    @Override
    @Transactional
    public SectionResponseDTO addSection(String idProject, SectionCreateRequestDTO requestDTO) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(idProject);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Project does not exist or has been deleted.");
        }

        Integer nextPosition = sectionRepository.findMaxPositionByProjectId(project.getId()) + 1;

        Section section = Section.builder().name(requestDTO.getName()).position(nextPosition).project(project).build();

        Section saved_section = sectionRepository.saveAndFlush(section);

        return sectionMapper.ResponseDTO(saved_section);
    }

    @Override
    @Transactional
    public SectionResponseDTO updateSection(String idSection, SectionUpdateRequestDTO requestDTO) {
        Section section = sectionRepository.findByIdAndIsDeletedFalse(idSection);

        if (section == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted");
        }

        int oldPosition = section.getPosition();
        int newPosition = requestDTO.getPosition();
        if (newPosition < oldPosition) {
            sectionRepository.shiftPositionsUp(section.getProject().getId(), newPosition, oldPosition);
        } else if (newPosition > oldPosition) {
            sectionRepository.shiftPositionsDown(section.getProject().getId(), newPosition, oldPosition);
        }
        section.setPosition(newPosition);
        Section saved = sectionRepository.saveAndFlush(section);

        return sectionMapper.ResponseDTO(saved);
    }

    @Override
    @Transactional
    public SectionResponseDTO removeSection(String idSection) {
        Section section = sectionRepository.findByIdAndIsDeletedFalse(idSection);

        if (section == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted");
        }

        // Kiểm tra xem project của section còn bao nhiêu section để tránh xóa hết section
        long totalSections = section.getProject().getSections().stream()
                .filter(s -> !s.getIsDeleted()) // chỉ lấy section chưa bị xóa
                .count();

        if (totalSections <= 1) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot delete because the project must have at least one section.");
        }


        // Xóa các task của section

        section.getTasks().forEach(task -> {
            taskService.applyRecursive(task, Task::softDelete, TaskComment::softDelete);
        });

        section.softDelete();

        // Chuyển position các section còn lại
        sectionRepository.shiftPositionsAfterDelete(section.getProject().getId(), section.getPosition());

        Section saved = sectionRepository.save(section);

        return sectionMapper.ResponseDTO(saved);
    }

    @Override
    @Transactional
    public SectionResponseDTO removeSectionAndMigrate(SectionDeleteAndMigrateDTO requestDTO) {

        // Tìm 2 section đích và nguồn
        Section sectionSource = sectionRepository.findByIdAndIsDeletedFalse(requestDTO.getIdSectionSource());

        if (sectionSource == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section resource doesn't exist or has been deleted");
        }

        Section sectionDestination = sectionRepository.findByIdAndIsDeletedFalse(requestDTO.getIdSectionDestination());

        if (sectionDestination == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section destination doesn't exist or has been deleted");
        }


        // Kiểm tra xem project của section còn bao nhiêu section để tránh xóa hết section
        long totalSections = sectionSource.getProject().getSections().stream()
                .filter(s -> !s.getIsDeleted()) // chỉ lấy section chưa bị xóa
                .count();

        if (totalSections <= 1) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot delete because the project must have at least one section.");
        }


        // Kiểm tra xem 2 section có cùng project không
        if (sectionSource.getProject() != sectionDestination.getProject()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Both sections must belong to the same project.");
        }


        // Chuyển task của section nguồn sang section đích
        sectionSource.getTasks().forEach(task -> {

            taskService.applyRecursive(task, t -> {
                t.setSection(sectionDestination);
            }, c -> {
            });

            taskRepository.save(task);
        });

        sectionSource.softDelete();

        // Chuyển position các section còn lại
        sectionRepository.shiftPositionsAfterDelete(sectionSource.getProject().getId(), sectionSource.getPosition());

        Section saved = sectionRepository.saveAndFlush(sectionSource);

        return sectionMapper.ResponseDTO(saved);
    }
}
