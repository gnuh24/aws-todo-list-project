package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.TaskCommentMapper;
import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.messaging.kafka.producer.KafkaNotificationProducer;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskCommentServiceImpl implements TaskCommentService {


    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Autowired
    private KafkaNotificationProducer kafkaNotificationProducer;


    @Override
    public TaskCommentResponseDTO addNewComment(TaskCommentRequestDTO requestDTO, String idTask, Account account) {

        Task task = getTaskAndCheck(idTask);

        TaskComment taskComment = TaskComment.builder().task(task).account(account).comment(requestDTO.getComment()).build();

        taskComment = taskCommentRepository.save(taskComment);


        System.err.println("Check");
        // ====== Gửi Kafka Notification ======


        // Danh sách người được gửi
        List<Account> listAccountReceiver = new ArrayList<>();
        listAccountReceiver.add(task.getAccountAssign());
        listAccountReceiver.add(task.getCreatedByAccount());

        for (TaskComment comment : task.getTaskComments()) {
            if (comment.getAccount() != account && comment.getAccount() != task.getCreatedByAccount() && comment.getAccount() != task.getAccountAssign()) {
                listAccountReceiver.add(comment.getAccount());
            }
        }


        for (Account accountReceiver : listAccountReceiver) {
            try {
                NotificationMessage message = NotificationMessage.builder()
                        .receiverId(accountReceiver.getId())   // người được nhận thông báo
                        .actorId(account.getId())                          // người thực hiện cập nhật task
                        .taskId(task.getId())
                        .projectId(task.getSection().getProject().getId())
                        .type(NotificationType.TASK_COMMENTED)
                        .title("Một bình luận mới được thêm vào Task!")
                        .content(String.format(
                                "\"%s\" vừa thêm bình luận mới vào task \"%s\" của dự án \"%s\".",
                                account.getDisplayName(),
                                task.getTitle(),
                                task.getSection().getProject().getName()
                        ))
                        .build();

                kafkaNotificationProducer.sendTaskCommented(message);

                System.out.printf("📤 [Kafka] Sent TASK_COMMENTED for task '%s' to account '%s'%n",
                        task.getTitle(), accountReceiver.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Gửi notification TASK_COMMENTED thất bại: " + e.getMessage());
            }
        }


        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    public TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment, Account account) {

        TaskComment taskComment = getCommentAndCheck(idComment, account);

        taskComment.setComment(requestDTO.getComment());

        taskComment = taskCommentRepository.save(taskComment);

        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    public TaskCommentResponseDTO deleteComment(String idComment, Account account) {

        TaskComment taskComment = getCommentAndCheck(idComment, account);

        taskComment.softDelete();

        taskComment = taskCommentRepository.save(taskComment);

        return taskCommentMapper.toResponse(taskComment);
    }


    private Task getTaskAndCheck(String idTask) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        if (task.getIsArchived()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot update task because its parent is archived.");
        }

        return task;
    }


    private TaskComment getCommentAndCheck(String idComment, Account account) {
        TaskComment taskComment = taskCommentRepository.findByIdAndIsDeletedFalse(idComment);

        if (taskComment == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Comment doesn't exist or has been deleted");
        }

        // Kiểm tra xem có đúng chính người tạo comment chỉnh sửa không
        if (!taskComment.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "You cannot edit comments created by other users.");
        }

        return taskComment;
    }
}
