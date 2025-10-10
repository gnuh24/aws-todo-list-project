package aws.todolist.taskflow.service;


import aws.todolist.taskflow.dto.task.*;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.entity.Section;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.Status;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.TaskMapper;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.SectionRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TaskMapper taskMapper;


    @Override
    public TaskDetailResponseDTO getTaskById(String idTask) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        return taskMapper.ResponseDetailDTO(task);
    }

    @Override
    public TaskResponseDTO addTask(String idProject, TaskCreateRequestDTO requestDTO) {

        Task taskFather = null;

        Section section = sectionRepository.findByIdAndIsDeletedFalse(requestDTO.getSectionId());

        if (section == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted");
        }

        // Kiểm tra section được dùng có đúng của project không
        if (!section.getProject().getId().equals(idProject)) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Section isn't belong to this project");
        }

        if (requestDTO.getTaskFatherId() != null) {
            taskFather = taskRepository.findByIdAndIsDeletedFalse(requestDTO.getTaskFatherId());
            if (taskFather == null) {
                throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task Father doesn't exist or has been deleted");
            }

            // Kiểm tra xem 2 task có cùng section không
            if (taskFather.getSection() != section) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "The two tasks are not in the same section — please move the task to the correct section first.");
            }
        }


        // TODO: KHÔNG CHO PHÉP ĐẶT THỜI GIAN Ở QUÁ KHỨ


        // Kiểm tra thời gian bắt đầu có bị lớn hơn thời gian kết thúc không
        if (requestDTO.getStartTime() != null && requestDTO.getDeadline() != null) {
            if (requestDTO.getStartTime().isAfter(requestDTO.getDeadline())) {
                throw new BadRequestException(
                        SystemErrorCode.API_BAD_REQUEST,
                        "Start time cannot be after the deadline."
                );
            }
        }

        Task task = Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .priority(requestDTO.getPriority() != null ? requestDTO.getPriority() : null)
                .deadline(requestDTO.getDeadline())
                .startTime(requestDTO.getStartTime())
                .isPinned(requestDTO.getIsPinned() != null ? requestDTO.getIsPinned() : false)
                .isArchived(requestDTO.getIsArchived() != null ? requestDTO.getIsArchived() : false)
                .taskFather(taskFather)
                .section(section)
                .build();

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updatePriority(String idTask, TaskUpdatePriorityRequestDTO requestDTO) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        task.setPriority(requestDTO.getPriority());

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updateRelationship(String idTask, TaskUpdateRelationshipRequestDTO requestDTO) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        // Kiểm tra xem người dùng muốn xóa task cha hay thêm task cha
        if (requestDTO.getIdTaskFather() != null) {
            Task taskFather = taskRepository.findByIdAndIsDeletedFalse(requestDTO.getIdTaskFather());

            if (taskFather == null) {
                throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task father doesn't exist or has been deleted");
            }

            // Kiểm tra xem 2 task cha và task con có cùng section không

            if (taskFather.getSection() != task.getSection()) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "The two tasks are not in the same section — please move the task to the correct section first.");
            }

            // Kiểm tra xem 2 task cha và task con có bị tạo ra mối quan hệ vòng tròn không

            if (taskFather.getTaskFather() == task) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Creating this task relationship would result in a circular dependency — please adjust the parent/child tasks to avoid loops.");
            }


            task.setTaskFather(taskFather);
        } else {
            task.setTaskFather(null);
        }


        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updateStatus(String idTask, TaskUpdateStatusRequestDTO requestDTO, Account account) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        // Kiểm tra xem task có được phân công chưa nếu có thì kiểm tra xem tài khoản đang thực thi có phải người được phân công không
        if (task.getAccountAssign() != null && !task.getAccountAssign().getId().equals(account.getId())) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "Task has been assigned for other. You can't change status of it");
        }

        // Kiểm tra thời gian thực hiện
        LocalDateTime now = LocalDateTime.now();

        if (task.getStartTime() != null && now.isBefore(task.getStartTime())) {
            throw new BadRequestException(
                    SystemErrorCode.API_BAD_REQUEST,
                    "You cannot change the status before the task start time."
            );
        }

        if (task.getDeadline() != null && now.isAfter(task.getDeadline())) {
            throw new BadRequestException(
                    SystemErrorCode.API_BAD_REQUEST,
                    "You cannot change the status after the task deadline."
            );
        }

        // Kiểm tra trạng thái người dùng tính cập nhật là gì. Nếu là completed thì thêm thời gian vào cập nhật vào
        if (requestDTO.getStatus() == Status.COMPLETED) {
            task.setCompletedAt(now);
        } else {
            task.setCompletedAt(null);
        }

        task.setStatus(requestDTO.getStatus());

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO assignTask(String idTask, String idProject, TaskAssignRequestDTO requestDTO) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        // Kiểm tra xem tài khoản có phải member của project và có vai trò gì

        Member member = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(requestDTO.getIdAccount(), idProject).orElseThrow(() -> new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Account is not a member of this project"));

        // Kiểm tra quyền của account
        if (member.getRole() == Role.ADMIN || member.getRole() == Role.VIEWER) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "Account do not have permission to complete this task");
        }

        task.setAccountAssign(member.getAccount());

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updateSectionForTask(String idTask, String idProject, TaskUpdateSectionRequestDTO requestDTO) {

        Section section = sectionRepository.findByIdAndIsDeletedFalse(requestDTO.getIdSection());

        if (section == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted");
        }

        // Kiểm tra section được dùng có đúng của project không
        if (!section.getProject().getId().equals(idProject)) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Section isn't belong to this project");
        }

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        // Hủy mối quan hệ cha con của task khi chuyển section (Sẽ bao phủ được 2 trường hợp là task con và task cha)
        task.setTaskFather(null);

        // Chuyển section cho task con của task hiện tại nếu có
        task.getTaskChild().forEach(taskChild -> {
            taskChild.setSection(section);
            taskRepository.save(taskChild);
        });

        task.setSection(section);

        task = taskRepository.save(task);


        return taskMapper.ResponseDTO(task);
    }


}
