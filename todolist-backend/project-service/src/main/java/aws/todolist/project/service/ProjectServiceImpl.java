package aws.todolist.project.service;

import aws.todolist.project.context.RequestContext;
import aws.todolist.project.dto.project.ProjectCreateRequestDTO;
import aws.todolist.project.dto.project.ProjectDetailResponseDTO;
import aws.todolist.project.dto.project.ProjectResponseDTO;
import aws.todolist.project.dto.project.ProjectUpdateRequestDTO;
import aws.todolist.project.entity.Account;
import aws.todolist.project.entity.Member;
import aws.todolist.project.entity.Project;
import aws.todolist.project.entity.Section;
import aws.todolist.project.enums.Role;
import aws.todolist.project.enums.StatusMember;
import aws.todolist.project.exceptions.ProjectException.BadRequestException;
import aws.todolist.project.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.project.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.project.exceptions.errorCode.SystemErrorCode;
import aws.todolist.project.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.project.mapper.ActorMapper;
import aws.todolist.project.mapper.ProjectMapper;
import aws.todolist.project.repository.MemberRepository;
import aws.todolist.project.repository.ProjectRepository;
import aws.todolist.project.repository.SectionRepository;
import aws.todolist.project.service.ServiceEventKafka.ProjectEventService;
import aws.todolist.project.service.ServiceInterface.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
// TODO: quyền OWNER thêm, sửa, xóa. Quyền khác là xem
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;


    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private GenericEventPublisher eventPublisher;

    @Autowired
    private ProjectEventService projectEventService;


    @Override
    public List<ProjectResponseDTO> getAllProject() {

        Account account = RequestContext.getAccount();

        List<Project> projects = projectRepository.findAllByAccountId(account.getId());

        return projectMapper.toResponseList(projects);

    }

    @Override
    public ProjectDetailResponseDTO getProjectById(String projectId) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(projectId);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Dự án không tồn tại hoặc đã bị xóa.");
        }

        return projectMapper.toDetailResponse(project);
    }


    @Transactional
    @Override
    public ProjectResponseDTO addProject(ProjectCreateRequestDTO projectCreateRequestDTO) {

        Account actor = RequestContext.getAccount();

        Project OptProjectDefault = projectRepository.findProjectIsDefault(actor.getId());

        // Kiểm tra dự án default
        if (projectCreateRequestDTO.getIsDefault() && OptProjectDefault != null) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Tài khoản đã có dự án mặc định");
        }


        Project project = Project.builder()
                .name(projectCreateRequestDTO.getName())
                .isArchived(projectCreateRequestDTO.getIsArchived() != null ? projectCreateRequestDTO.getIsArchived() : false)
                .isDefault(projectCreateRequestDTO.getIsDefault() != null ? projectCreateRequestDTO.getIsDefault() : false)
                .build();


        Project saved_project = projectRepository.saveAndFlush(project);

        Member member = Member.builder().account(actor).project(saved_project).role(Role.OWNER).status(StatusMember.ACCEPTED).build();

        Section section = Section.builder().project(saved_project).name("Section default").position(1).build();

        memberRepository.save(member);

        sectionRepository.save(section);

        saved_project.getMembers().add(member);

        saved_project.getSections().add(section);

        // ------------------------------------------
        // Gửi event kafka cho websocket
        // ------------------------------------------

        projectEventService.publishProjectCreated(saved_project);


        return projectMapper.toResponse(saved_project);
    }

    @Transactional
    @Override
    public ProjectResponseDTO updateProject(String id, ProjectUpdateRequestDTO projectUpdateRequestDTO) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Dự án không tồn tại hoặc đã bị xóa.");
        }

        if (projectUpdateRequestDTO.getName() != null) {
            project.setName(projectUpdateRequestDTO.getName());
        }

        if (projectUpdateRequestDTO.getIsArchived() != null) {
            project.setIsArchived(projectUpdateRequestDTO.getIsArchived());
        }

        Project saved_project = projectRepository.saveAndFlush(project);

        // Gửi event kafka cho websocket
        projectEventService.publishProjectUpdated(saved_project);

        return projectMapper.toResponse(saved_project);
    }

    @Transactional
    @Override
    public ProjectResponseDTO removeProject(String id) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Dự án không tồn tại hoặc đã bị xóa.");
        }

        // Kiểm tra project có phải là default không
        if (project.getIsDefault()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Không thể xóa dự án mặc định");
        }


        // Chạy vòng lặp để cập nhật các section thành deleted và chuyển task về project default

        Account actor = RequestContext.getAccount();

        Project defaultProject = projectRepository.findProjectIsDefault(actor.getId());

        if (defaultProject == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Account không có project mặc định");
        }

        Section sectionDefault = defaultProject.getSections().getFirst();

        // Chạy vòng lặp để cập nhật các section thành deleted và chuyển task về project default
        project.getSections().forEach(Section::softDelete);

        // -------------------------------
        // Gửi event lên kafka để cập nhật
        // -------------------------------
        projectEventService.publishProjectDeleted(project);


        // Cập nhật các member về deleted
        project.getMembers().forEach(Member::softDelete);

        project.softDelete();

        Project saved = projectRepository.saveAndFlush(project);


        return projectMapper.toResponse(saved);
    }
}
