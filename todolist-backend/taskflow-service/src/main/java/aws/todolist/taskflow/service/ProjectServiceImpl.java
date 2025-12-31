package aws.todolist.taskflow.service;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.project.ProjectCreateRequestDTO;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectUpdateRequestDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.entity.Section;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.ProjectMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import aws.todolist.taskflow.repository.SectionRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.service.ServiceEventKafka.ProjectEventService;
import aws.todolist.taskflow.service.ServiceInterface.AccountService;
import aws.todolist.taskflow.service.ServiceInterface.ProjectService;
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
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private GenericEventPublisher eventPublisher;

    @Autowired
    private ProjectEventService projectEventService;

    @Autowired
    private AccountService accountService;


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

        Project project = Project.builder()
                .name(projectCreateRequestDTO.getName())
                .isArchived(projectCreateRequestDTO.getIsArchived() != null ? projectCreateRequestDTO.getIsArchived() : false)
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

        Project defaultProject = projectRepository.findProjectDefault(actor.getId());

        if (defaultProject == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Account không có project mặc định");
        }

        Section sectionDefault = defaultProject.getSections().getFirst();

        // Chạy vòng lặp để cập nhật các section thành deleted và chuyển task về project default
        project.getSections().forEach(section -> {
            section.softDelete();

            section.getTasks().forEach(task -> {
                taskService.applyRecursive(task, t -> t.setSection(sectionDefault), c -> {
                });
                taskRepository.save(task);
            });
        });

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

    @Override
    public void addProjectDefault(String idAccount) {
        Account actor = accountService.getAccountById(idAccount);

        Project project = Project.builder()
                .name("Inbox")
                .isArchived(false)
                .isDefault(true)
                .build();


        Project saved_project = projectRepository.saveAndFlush(project);

        Member member = Member.builder().account(actor).project(saved_project).role(Role.OWNER).status(StatusMember.ACCEPTED).build();

        Section section = Section.builder().project(saved_project).name("Section default").position(1).build();

        memberRepository.save(member);

        sectionRepository.save(section);

        saved_project.getMembers().add(member);

        saved_project.getSections().add(section);

        System.err.println("tạo project default");

        // ------------------------------------------
        // Gửi event kafka cho websocket
        // ------------------------------------------

        projectEventService.publishProjectCreated(saved_project);
    }

    @Override
    public ProjectDetailResponseDTO getProjectDefault() {

        Account actor = RequestContext.getAccount();

        Project projectDefault = projectRepository.findProjectDefault(actor.getId());

        return projectMapper.toDetailResponse(projectDefault);
    }
}
