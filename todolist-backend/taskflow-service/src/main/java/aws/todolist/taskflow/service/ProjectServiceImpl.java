package aws.todolist.taskflow.service;

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
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.ProjectMapper;
import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.messaging.kafka.producer.KafkaNotificationProducer;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import aws.todolist.taskflow.repository.SectionRepository;
import aws.todolist.taskflow.repository.TaskRepository;
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
    private MemberRepository memberRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private KafkaNotificationProducer kafkaNotificationProducer;

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
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Project does not exist or has been deleted.");
        }

        return projectMapper.ResponseDTODetail(project);
    }


    @Transactional
    @Override
    public ProjectResponseDTO addProject(ProjectCreateRequestDTO projectCreateRequestDTO, Account account) {

        Project OptProjectDefault = projectRepository.findProjectIsDefault(account.getId());

        // Kiểm tra dự án default
        if (projectCreateRequestDTO.getIsDefault() && OptProjectDefault != null) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Account has default project");
        }


        Project project = Project.builder()
                .name(projectCreateRequestDTO.getName())
                .isArchived(projectCreateRequestDTO.getIsArchived() != null ? projectCreateRequestDTO.getIsArchived() : false)
                .isDefault(projectCreateRequestDTO.getIsDefault() != null ? projectCreateRequestDTO.getIsDefault() : false)
                .build();


        Project saved = projectRepository.saveAndFlush(project);

        Member member = Member.builder().account(account).project(saved).role(Role.OWNER).status(StatusMember.ACCEPTED).build();

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
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Project does not exist or has been deleted.");
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
    public ProjectResponseDTO removeProject(String id, Account account) {

        Optional<Project> optProject = projectRepository.findByIdAndIsDeletedFalse(id);

        Project project;

        if (optProject.isPresent()) {
            project = optProject.get();
        } else {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Project does not exist or has been deleted.");
        }

        // Kiểm tra project có phải là default không
        if (project.getIsDefault()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Can't delete: This project is default project");
        }


        // Chạy vòng lặp để cập nhật các section thành deleted và chuyển task về project default

        Project defaultProject = projectRepository.findProjectIsDefault(account.getId());

        if (defaultProject == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "This account doesn't have default project");
        }

        Section sectionDefault = defaultProject.getSections().getFirst();

        project.getSections().forEach(section -> {
            section.softDelete();

            section.getTasks().forEach(task -> {
                taskService.applyRecursive(task, t -> t.setSection(sectionDefault), c -> {
                });
                taskRepository.save(task);
            });
        });

        // Cập nhật các member về deleted
        project.getMembers().forEach(Member::softDelete);

        project.softDelete();

        Project saved = projectRepository.saveAndFlush(project);


        System.err.println("Check");
        // ====== Gửi Kafka Notification ======


        for (Member member : project.getMembers()) {
            try {
                NotificationMessage message = NotificationMessage.builder()
                        .receiverId(member.getAccount().getId())   // người được nhận thông báo
                        .actorId(account.getId())                          // người thực hiện cập nhật task
                        .projectId(project.getId())
                        .type(NotificationType.PROJECT_DELETED)
                        .title("Dự án đã bị xóa!")
                        .content(String.format(
                                "Dự án \"%s\" đã bị \"%s\" xóa khỏi hệ thống.",
                                project.getName(),
                                account.getDisplayName()
                        ))
                        .build();

                kafkaNotificationProducer.sendProjectDeleted(message);

                System.out.printf("📤 [Kafka] Sent PROJECT_DELETED to account '%s'%n", member.getAccount().getEmail());
            } catch (Exception e) {
                System.err.println("❌ Gửi notification PROJECT_DELETED thất bại: " + e.getMessage());
            }
        }

        return projectMapper.ResponseDTO(saved);
    }

//    @Transactional
//    @Override
//    public ProjectResponseDTO restoreProject(String id) {
//
//        Optional<Project> optProject = projectRepository.findById(id);
//
//        Project project;
//
//        if (optProject.isPresent()) {
//            project = optProject.get();
//        } else {
//            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Project không tồn tại");
//        }
//
//        project.getSections().forEach(section -> {
//            section.restore();
//            section.getTasks().forEach(task -> {
//                task.restore();
//                task.getTaskComments().forEach(TaskComment::restore);
//            });
//        });
//
//        project.restore();
//
//        Project saved = projectRepository.saveAndFlush(project);
//        return projectMapper.ResponseDTO(saved);
//
//    }
}
