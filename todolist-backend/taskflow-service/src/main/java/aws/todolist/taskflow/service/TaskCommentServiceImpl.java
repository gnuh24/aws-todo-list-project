package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.event.payload.CommentPayload;
import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.TaskCommentMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.utils.NotificationUtils;
import aws.todolist.taskflow.utils.TaskCommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private GenericEventPublisher genericEventPublisher;

    @Autowired
    private ActorMapper actorMapper;

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

        Set<String> receiverSet = new HashSet<>();

        for (TaskComment taskCommentInTask : task.getTaskComments()) {
            receiverSet.add(taskCommentInTask.getAccount().getId());
        }

        List<String> receivers = new ArrayList<>(receiverSet);

        CommentPayload payload = taskCommentMapper.toPayload(taskComment, actorMapper.toActorDto(accountLogging), receivers);
        genericEventPublisher.publishCommentEvent(task.getSection().getProject().getId(), payload, EventType.COMMENT_CREATED);

        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment, Account actor) {

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, actor);

        if (requestDTO.getComment() != null) {
            taskComment.setComment(requestDTO.getComment());
        }


        // Chạy service thêm mới comment attach
        if (requestDTO.getUrls() != null) {
            for (String url : requestDTO.getUrls()) {
                try {
                    taskComment.getCommentAttaches().add(commentAttachService.addNewCommentAttach(url, taskComment.getId(), actor));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        taskComment = taskCommentRepository.saveAndFlush(taskComment);


        CommentPayload payload = taskCommentMapper.toPayload(taskComment, actorMapper.toActorDto(actor), null);
        genericEventPublisher.publishCommentEvent(taskComment.getTask().getSection().getProject().getId(), payload, EventType.COMMENT_UPDATED);


        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO deleteComment(String idComment, Account actor) {

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, actor);

        taskComment.softDelete();

        commentAttachService.deleteCommentAttachByIdComment(taskComment.getId());

        taskComment = taskCommentRepository.save(taskComment);

        CommentPayload payload = taskCommentMapper.toPayload(taskComment, actorMapper.toActorDto(actor), null);
        genericEventPublisher.publishCommentEvent(taskComment.getTask().getSection().getProject().getId(), payload, EventType.COMMENT_DELETED);


        return taskCommentMapper.toResponse(taskComment);
    }


    private Task getTaskAndCheck(String idTask) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Task không tồn tại hoặc đã bị xóa");
        }

        return task;
    }

}
