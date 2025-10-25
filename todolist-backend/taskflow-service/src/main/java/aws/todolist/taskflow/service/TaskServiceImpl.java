package aws.todolist.taskflow.service;


import aws.todolist.taskflow.dto.task.*;
import aws.todolist.taskflow.entity.*;
import aws.todolist.taskflow.enums.Priority;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.Status;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.TaskMapper;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.quartzScheduler.TaskSchedulerService;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.SectionRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.utils.NotificationUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Consumer;

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

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Autowired
    private NotificationUtils notificationUtils;


    @Override
    public TaskDetailResponseDTO getTaskById(String idTask) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        return taskMapper.ResponseDetailDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO addTask(String idProject, TaskCreateRequestDTO requestDTO, Account accountLogging) {

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
            taskFather = getTaskAndCheck(requestDTO.getTaskFatherId());

            // Kiểm tra xem 2 task có cùng section không
            if (taskFather.getSection() != section) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "The two tasks are not in the same section — please move the task to the correct section first.");
            }

            if (taskFather.getIsArchived()) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot update task because its parent is archived.");
            }
        }


        // TODO: KHÔNG CHO PHÉP ĐẶT THỜI GIAN Ở QUÁ KHỨ
        LocalDateTime now = LocalDateTime.now();

        if (requestDTO.getStartTime() != null) {
            if (requestDTO.getStartTime().isBefore(now)) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Start time must be in the future.");
            }
        }

        // Cập nhật deadline nếu có
        if (requestDTO.getDeadline() != null) {
            LocalDateTime referenceTime = requestDTO.getStartTime(); // ưu tiên startTime mới nếu đã cập nhật
            if (referenceTime != null) {
                // Nếu có startTime, deadline phải sau startTime
                if (!requestDTO.getDeadline().isAfter(referenceTime)) {
                    throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Deadline must be after start time.");
                }
            } else {
                // Nếu không có startTime, deadline phải sau hiện tại
                if (requestDTO.getDeadline().isBefore(now)) {
                    throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Deadline must be in the future.");
                }
            }
        }

        // Kiểm tra xem người dùng có phân công task luôn không

        Member member = null;

        if (requestDTO.getIdAccountAssign() != null) {
            member = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(requestDTO.getIdAccountAssign(), idProject).orElseThrow(() -> new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Account is not a member of this project"));

            if (member.getStatus() != StatusMember.ACCEPTED) {
                throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "The accountLogging has not accepted the invitation to join this project");
            }

            // Kiểm tra quyền của accountLogging
            if (member.getRole() == Role.ADMIN || member.getRole() == Role.VIEWER) {
                throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "Account do not have permission to complete this task");
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
                .createdByAccount(accountLogging)
                .accountAssign(member != null ? member.getAccount() : null)
                .section(section)
                .build();

        task = taskRepository.save(task);

        // Thông báo kafka nếu task được phân công luôn

        System.out.println(task.getId());


        if (task.getAccountAssign() != null) {
            // Thông báo kafka


            // Đặt actor là người tạo task
            notificationUtils.sendNotification(task, task.getSection().getProject(), accountLogging, notificationUtils.getReceiversForTask(task), NotificationType.TASK_ASSIGNED);
        }

        // === Đặt scheduler nếu task mới tạo có đặt deadline

        if (task.getDeadline() != null) {
            try {
                taskSchedulerService.scheduleTaskWithReminders(task.getId(), task.getDeadline());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }

        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO updatePriority(String idTask, TaskUpdatePriorityRequestDTO requestDTO) {

        Task task = this.getTaskAndCheck(idTask);

        task.setPriority(requestDTO.getPriority());

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateRelationship(String idTask, TaskUpdateRelationshipRequestDTO requestDTO) {

        Task task = this.getTaskAndCheck(idTask);

        // Kiểm tra xem người dùng muốn xóa task cha hay thêm task cha
        if (requestDTO.getIdTaskFather() != null) {
            Task taskFather = getTaskAndCheck(requestDTO.getIdTaskFather());

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
    @Transactional
    // Phải lên lịch nhắc hẹn vì có dùng tới trạng thái hoàn thành của task
    public TaskResponseDTO updateStatus(String idTask, TaskUpdateStatusRequestDTO requestDTO, Account accountLogging) {

        Task task = this.getTaskAndCheck(idTask);

        // Kiểm tra xem task có được phân công chưa nếu có thì kiểm tra xem tài khoản đang thực thi có phải người được phân công không
        if (task.getAccountAssign() != null && !task.getAccountAssign().getId().equals(accountLogging.getId())) {
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

            // Thông báo kafka

            // Actor để null để hàm sendNotification tự lấy accountLogging đang đăng nhập
            notificationUtils.sendNotification(task, task.getSection().getProject(), null, notificationUtils.getReceiversForTask(task), NotificationType.TASK_COMPLETED);


            // Nếu task có deadline thì xóa lịch thông báo

            try {
                if (taskSchedulerService.isJobExists(task.getId())) {
                    taskSchedulerService.deleteTaskSchedule(task.getId());
                }
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }


        } else {

            // Nếu có deadline thì cập nhật lịch thông báo trở lại

            if (task.getDeadline() != null) {
                try {

                    // Lên lịch thông báo sắp đến hạn và trễ hạn
                    taskSchedulerService.scheduleTaskWithReminders(task.getId(), task.getDeadline());

                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            }

            task.setCompletedAt(null);
        }

        task.setStatus(requestDTO.getStatus());

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO assignTask(String idTask, String idProject, TaskAssignRequestDTO requestDTO) {

        // ====== Lấy task ======
        Task task = this.getTaskAndCheck(idTask);

        // ====== Kiểm tra membership ======
        Member member = memberRepository
                .findFirstByAccountIdAndProjectIdAndIsDeletedFalse(requestDTO.getIdAccount(), idProject)
                .orElseThrow(() -> new ResourceNotFoundException(
                        SystemErrorCode.SYS_OBJECT_NOT_FOUND,
                        "Account is not a member of this project"
                ));

        // ====== Kiểm tra quyền ======
        if (member.getRole() == Role.ADMIN || member.getRole() == Role.VIEWER) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED,
                    "Account does not have permission to complete this task");
        }

        // Kiểm tra phân công có bị trùng ko
        if (task.getAccountAssign() == member.getAccount()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Account has been assigned to this task before");
        }

        // ====== Cập nhật người được giao ======
        task.setAccountAssign(member.getAccount());
        task = taskRepository.save(task);


        // Thông báo kafka


        notificationUtils.sendNotification(task, task.getSection().getProject(), null, notificationUtils.getReceiversForTask(task), NotificationType.TASK_ASSIGNED);


        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO assigneeTask(String idTask) {

        Task task = getTaskAndCheck(idTask);

        if (task.getAccountAssign() == null) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Task hasn't been assigned yet");
        }

        task.setAccountAssign(null);

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateSectionForTask(String idTask, String idProject, TaskUpdateSectionRequestDTO requestDTO) {

        Section section = sectionRepository.findByIdAndIsDeletedFalse(requestDTO.getIdSection());

        if (section == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted");
        }

        // Kiểm tra section được dùng có đúng của project không
        if (!section.getProject().getId().equals(idProject)) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Section isn't belong to this project");
        }

        Task task = this.getTaskAndCheck(idTask);

        // Hủy mối quan hệ cha con của task khi chuyển section (Sẽ bao phủ được 2 trường hợp là task con và task cha)
        task.setTaskFather(null);

        // Chuyển section cho task con của task hiện tại nếu có
        task.getTaskChild().forEach(taskChild -> {
            this.applyRecursive(taskChild, t -> t.setSection(section), c -> {
            });
            taskRepository.save(taskChild);
        });

        task.setSection(section);

        task = taskRepository.save(task);


        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(String idTask, TaskUpdateRequestDTO requestDTO) {

        Task task = this.getTaskAndCheck(idTask);

        if (requestDTO.getTitle() != null) {
            task.setTitle(requestDTO.getTitle());
        }

        if (requestDTO.getDescription() != null) {
            task.setDescription(requestDTO.getDescription());
        }

        LocalDateTime now = LocalDateTime.now();

        if (requestDTO.isStartTimeSent()) {
            LocalDateTime newStart = requestDTO.getStartTime(); // có thể null
            if (newStart != null && newStart.isBefore(now)) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Start time must be in the future.");
            }
            task.setStartTime(newStart);
        }

        updateDeadline(task, requestDTO);


        if (requestDTO.getPriority() != null) {
            task.setPriority(Priority.valueOf(requestDTO.getPriority()));
        }

        if (requestDTO.getIsPinned() != null) {
            task.setIsPinned(requestDTO.getIsPinned());
        }


        task = taskRepository.save(task);


        // Thông báo kafka

        notificationUtils.sendNotification(task, task.getSection().getProject(), null, notificationUtils.getReceiversForTask(task), NotificationType.TASK_UPDATED);


        // ====== Kiểm tra xem task có deadline ko nếu có thì cập nhật lại việc lên lịch

        if (task.getDeadline() != null) {
            try {
                // Lên lịch thông báo sắp đến hạn và trễ hạn
                taskSchedulerService.scheduleTaskWithReminders(task.getId(), task.getDeadline());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        } else {

            // Xóa lịch cũ trước khi action
            try {
                if (taskSchedulerService.isJobExists(task.getId())) {

                    // Xóa thông báo sắp đến hạn và trễ hạn
                    taskSchedulerService.deleteTaskSchedule(task.getId());

                }
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }


        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO archiveTask(String idTask, TaskArchivedRequestDTO requestDTO) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        task.setIsArchived(requestDTO.getIsArchived());

        task.getTaskChild().forEach(taskChild -> this.applyRecursive(taskChild, t -> t.setIsArchived(requestDTO.getIsArchived()), c -> {
        }));


        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO deleteTask(String idTask) {

        Task task = getTaskAndCheck(idTask);

        task.softDelete();


        task.getTaskComments().forEach(TaskComment::softDelete);

        task.getTaskChild().forEach(TaskChild -> this.applyRecursive(TaskChild, Task::softDelete, TaskComment::softDelete));

        task = taskRepository.save(task);

        // Xóa lịch cũ trước khi action
        try {
            if (taskSchedulerService.isJobExists(task.getId())) {
                taskSchedulerService.deleteTaskSchedule(task.getId());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }


        return taskMapper.ResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO restore(String idTask, String idProject) {

        Task task = taskRepository.findByIdAndIsDeletedTrue(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist");
        }

        // Kiểm tra lại section của task hiện tại xem còn không nếu không còn thì lấy 1 section trong project

        if (task.getSection().getIsDeleted()) {
            Section newSection = sectionRepository.findTopByProjectIdAndIsDeletedFalseOrderByPositionAsc(idProject);
            task.setSection(newSection);
            task.getTaskChild().forEach(TaskChild -> this.applyRecursive(TaskChild, t -> t.setSection(newSection), c -> {
            }));
        }

        task.restore();

        task.getTaskChild().forEach(TaskChild -> this.applyRecursive(TaskChild, Task::restore, TaskComment::restore));

        task = taskRepository.save(task);

        // 5️⃣ Restore / tạo lại lịch mới nếu có deadline và task chưa bị xóa
        if (task.getDeadline() != null && !task.getIsDeleted()) {
            try {
                taskSchedulerService.scheduleTaskWithReminders(task.getId(), task.getDeadline());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }


        return taskMapper.ResponseDTO(task);
    }


    private void updateDeadline(Task task, TaskUpdateRequestDTO requestDTO) {
        if (!requestDTO.isDeadlineSent()) {
            return; // client không gửi → không update
        }

        LocalDateTime newDeadline = requestDTO.getDeadline(); // có thể null
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        // chỉ validate khi newDeadline != null
        if (newDeadline != null) {
            if (startTime != null && !newDeadline.isAfter(startTime)) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST,
                        "Deadline must be after start time.");
            }
            if (startTime == null && newDeadline.isBefore(now)) {
                throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST,
                        "Deadline must be in the future.");
            }
        }

        // set deadline (có thể null → hủy bỏ)
        task.setDeadline(newDeadline);
    }

    private Task getTaskAndCheck(String idTask) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        if (task.getIsArchived()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot update task because it is archived.");
        }

        return task;
    }


    // Hàm sử dụng để áp dụng hành động lên task và đệ quy lên task con, comment
    public void applyRecursive(Task task, Consumer<Task> taskAction, Consumer<TaskComment> commentAction) {

        // Áp dụng hành động lên task
        taskAction.accept(task);


        // Xóa lịch cũ trước khi action
        try {
            if (taskSchedulerService.isJobExists(task.getId())) {
                taskSchedulerService.deleteTaskSchedule(task.getId());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }


        // Áp dụng hành động lên tất cả comment của task
        task.getTaskComments().forEach(commentAction);

        // Đệ quy xuống các child
        task.getTaskChild().forEach(child -> applyRecursive(child, taskAction, commentAction));


        // 5️⃣ Restore / tạo lại lịch mới nếu có deadline và task chưa bị xóa
        if (task.getDeadline() != null && !task.getIsDeleted()) {
            try {
                taskSchedulerService.scheduleTaskWithReminders(task.getId(), task.getDeadline());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
