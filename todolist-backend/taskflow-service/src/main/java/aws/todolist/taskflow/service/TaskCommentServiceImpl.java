package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.TaskCommentMapper;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.utils.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskCommentServiceImpl implements TaskCommentService {


    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Autowired
    private NotificationUtils notificationUtils;


    @Override
    @Transactional
    public TaskCommentResponseDTO addNewComment(TaskCommentRequestDTO requestDTO, String idTask, Account accountLogging) {

        Task task = getTaskAndCheck(idTask);

        TaskComment taskComment = TaskComment.builder().task(task).account(accountLogging).comment(requestDTO.getComment()).build();

        taskComment = taskCommentRepository.save(taskComment);


        System.err.println("Check");
        // ====== Gửi Kafka Notification ======


        notificationUtils.sendNotification(task, task.getSection().getProject(), accountLogging, notificationUtils.getReceiversForTaskComment(task), NotificationType.TASK_COMMENTED);


        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment, Account account) {

        TaskComment taskComment = getCommentAndCheck(idComment, account);

        taskComment.setComment(requestDTO.getComment());

        taskComment = taskCommentRepository.save(taskComment);

        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO deleteComment(String idComment, Account account) {

        TaskComment taskComment = getCommentAndCheck(idComment, account);

        taskComment.softDelete();

        taskComment = taskCommentRepository.save(taskComment);

        return taskCommentMapper.toResponse(taskComment);
    }


    private Task getTaskAndCheck(String idTask) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task không tồn tại hoặc đã bị xóa");
        }

        return task;
    }


    private TaskComment getCommentAndCheck(String idComment, Account account) {
        TaskComment taskComment = taskCommentRepository.findByIdAndIsDeletedFalse(idComment);

        if (taskComment == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Comment không tồn tại hoặc đã bị xóa");
        }

        // Kiểm tra xem có đúng chính người tạo comment chỉnh sửa không
        if (!taskComment.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "Bạn không thể chỉnh sửa comment của người khác");
        }

        return taskComment;
    }
}
