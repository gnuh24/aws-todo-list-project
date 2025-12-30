package aws.todolist.project.service;

import aws.todolist.project.dto.section.*;
import aws.todolist.project.entity.Project;
import aws.todolist.project.entity.Section;
import aws.todolist.project.exceptions.ProjectException.BadRequestException;
import aws.todolist.project.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.project.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.project.exceptions.errorCode.SystemErrorCode;
import aws.todolist.project.mapper.SectionMapper;
import aws.todolist.project.repository.MemberRepository;
import aws.todolist.project.repository.ProjectRepository;
import aws.todolist.project.repository.SectionRepository;
import aws.todolist.project.service.ServiceEventKafka.SectionEventService;
import aws.todolist.project.service.ServiceInterface.SectionService;
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
    private MemberRepository memberRepository;

    @Autowired
    private SectionEventService sectionEventService;


    @Override
    public List<SectionResponseDTO> getAllSection(String idProject) {

        List<Section> sections = sectionRepository.findByProjectIdAndIsDeletedFalseOrderByPositionAsc(idProject);

        return sectionMapper.toResponseList(sections);
    }

    @Override
    @Transactional
    public SectionResponseDTO addSection(String idProject, SectionCreateRequestDTO requestDTO) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(idProject);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Dự án không tồn tại hoặc đã bị xóa");
        }

        Integer nextPosition = sectionRepository.findMaxPositionByProjectId(project.getId()) + 1;

        Section section = Section.builder().name(requestDTO.getName()).position(nextPosition).project(project).build();

        Section saved_section = sectionRepository.saveAndFlush(section);


        // =============================
        // 🔔 Gửi Kafka
        // =============================
        sectionEventService.publishSectionCreated(saved_section);


        return sectionMapper.toResponse(saved_section);
    }

    @Override
    @Transactional
    public SectionResponseDTO updatePositionSection(String idSection, SectionUpdateRequestDTO requestDTO) {
        Section section = sectionRepository.findByIdAndIsDeletedFalse(idSection);

        if (section == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Section không tồn tại hoặc đã bị xóa");
        }

        int oldPosition = section.getPosition();
        int newPosition = requestDTO.getPosition();
        if (newPosition < oldPosition) {
            sectionRepository.shiftPositionsUp(section.getProject().getId(), newPosition, oldPosition);
        } else if (newPosition > oldPosition) {
            sectionRepository.shiftPositionsDown(section.getProject().getId(), newPosition, oldPosition);
        }
        section.setPosition(newPosition);


        Section saved_section = sectionRepository.saveAndFlush(section);

        // =============================
        // 🔔 Gửi Kafka
        // =============================

        sectionEventService.publishSectionMoved(saved_section);

        return sectionMapper.toResponse(saved_section);
    }

    @Override
    public SectionResponseDTO updateNameSection(String idSection, SectionUpdateNameDTO requestDTO) {
        Section section = sectionRepository.findByIdAndIsDeletedFalse(idSection);

        if (section == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Section không tồn tại hoặc đã bị xóa");
        }

        section.setName(requestDTO.getName());

        Section saved_section = sectionRepository.saveAndFlush(section);

        // =============================
        // 🔔 Gửi Kafka
        // =============================

        sectionEventService.publishSectionNameUpdated(saved_section);


        return sectionMapper.toResponse(saved_section);
    }

    @Override
    @Transactional
    public SectionResponseDTO removeSection(String idSection) {
        Section section = sectionRepository.findByIdAndIsDeletedFalse(idSection);

        if (section == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Section không tồn tại hoặc đã bị xóa");
        }

        // Kiểm tra xem project của section còn bao nhiêu section để tránh xóa hết section
        long totalSections = section.getProject().getSections().stream()
                .filter(s -> !s.getIsDeleted()) // chỉ lấy section chưa bị xóa
                .count();

        if (totalSections <= 1) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Không thể xóa vì project cần ít nhật một section.");
        }


        section.softDelete();

        // Chuyển position các section còn lại
        sectionRepository.shiftPositionsAfterDelete(section.getProject().getId(), section.getPosition());

        Section saved_section = sectionRepository.save(section);

        // =============================
        // 🔔 Gửi Kafka
        // =============================
        sectionEventService.publishSectionDeleted(saved_section);

        return sectionMapper.toResponse(saved_section);
    }
}
