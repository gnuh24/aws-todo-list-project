package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.section.SectionCreateRequestDTO;
import aws.todolist.taskflow.dto.section.SectionDeleteAndMigrateDTO;
import aws.todolist.taskflow.dto.section.SectionResponseDTO;
import aws.todolist.taskflow.dto.section.SectionUpdateRequestDTO;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.entity.Section;
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
        Section section = sectionRepository.findById(idSection)
                .orElseThrow(() -> new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Section doesn't exist or has been deleted"));
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
        Section section = sectionRepository.findById(idSection)
                .orElseThrow(() -> new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted"));

        // Xóa các task của section

        section.getTasks().forEach(task -> {
            task.softDelete();
            task.getTaskComments().forEach(TaskComment::softDelete);
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
        Section sectionSource = sectionRepository.findById(requestDTO.getIdSectionSource())
                .orElseThrow(() -> new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section resource doesn't exist or has been deleted"));

        Section sectionDestination = sectionRepository.findById(requestDTO.getIdSectionDestination())
                .orElseThrow(() -> new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section destination doesn't exist or has been deleted"));

        // Chuyển task của section nguồn sang section đích
        sectionSource.getTasks().forEach(task -> {
            task.setSection(sectionDestination);
            taskRepository.save(task);
        });

        sectionSource.softDelete();

        // Chuyển position các section còn lại
        sectionRepository.shiftPositionsAfterDelete(sectionSource.getProject().getId(), sectionSource.getPosition());

        Section saved = sectionRepository.saveAndFlush(sectionSource);

        return sectionMapper.ResponseDTO(saved);
    }
}
