package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.TaskCommentMapper;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.utils.NotificationUtils;
import aws.todolist.taskflow.utils.TaskCommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

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

    @Autowired
    private TaskCommentUtils taskCommentUtils;

    @Autowired
    private CommentAttachService commentAttachService;


    @Override
    @Transactional
    public TaskCommentResponseDTO addNewComment(TaskCommentRequestDTO requestDTO, String idTask, Account accountLogging) {

        Task task = getTaskAndCheck(idTask);

        TaskComment taskComment = TaskComment.builder().task(task).account(accountLogging).comment(requestDTO.getComment()).build();

        taskComment = taskCommentRepository.saveAndFlush(taskComment);

        // Chạy service thêm mới comment attach
        if (requestDTO.getUrls() != null) {
            for (String url : requestDTO.getUrls()) {
                try {

                    taskComment.getCommentAttaches().add(commentAttachService.addNewCommentAttach(url, taskComment.getId(), accountLogging));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        System.err.println("Check");
        // ====== Gửi Kafka Notification ======


        notificationUtils.sendNotification(task, task.getSection().getProject(), accountLogging, notificationUtils.getReceiversForTaskComment(task), NotificationType.TASK_COMMENTED);


        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment, Account account) {

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, account);

        if (requestDTO.getComment() != null) {
            taskComment.setComment(requestDTO.getComment());
        }


        // Chạy service thêm mới comment attach
        if (requestDTO.getUrls() != null) {
            for (String url : requestDTO.getUrls()) {
                try {
                    taskComment.getCommentAttaches().add(commentAttachService.addNewCommentAttach(url, taskComment.getId(), account));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        taskComment = taskCommentRepository.saveAndFlush(taskComment);


        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO deleteComment(String idComment, Account account) {

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, account);

        taskComment.softDelete();

        commentAttachService.deleteCommentAttachByIdComment(taskComment.getId());

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

}
