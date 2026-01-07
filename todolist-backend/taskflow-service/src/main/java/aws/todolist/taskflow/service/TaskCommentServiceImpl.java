package aws.todolist.taskflow.service;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.CommentAttach;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.mapper.TaskCommentMapper;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.service.ServiceEventKafka.CommentEventService;
import aws.todolist.taskflow.service.ServiceInterface.CommentAttachService;
import aws.todolist.taskflow.service.ServiceInterface.TaskCommentService;
import aws.todolist.taskflow.utils.TaskCommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private TaskCommentUtils taskCommentUtils;

    @Autowired
    private CommentAttachService commentAttachService;

    @Autowired
    private CommentEventService commentEventService;


    @Override
    @Transactional
    public TaskCommentResponseDTO addNewComment(TaskCommentRequestDTO requestDTO, String idTask) {
        Task task = getTaskAndCheck(idTask);
        Account actor = RequestContext.getAccount();

        // 1️⃣ Tạo comment chính
        TaskComment taskComment = TaskComment.builder()
                .task(task)
                .account(actor)
                .comment(requestDTO.getComment())
                .build();
        taskComment = taskCommentRepository.save(taskComment);

        // 2️⃣ Tạo CommentAttach riêng, không trực tiếp thêm vào collection managed
        List<CommentAttach> attachList = new ArrayList<>();
        if (requestDTO.getUrls() != null) {
            for (String url : requestDTO.getUrls()) {
                CommentAttach attach = commentAttachService.addNewCommentAttach(url, taskComment.getId(), actor);
                attachList.add(attach);
            }
        }

        // 3️⃣ Gán list attach mới vào taskComment
        taskComment.setCommentAttaches(attachList);

        // 4️⃣ Gửi event sau khi tất cả entity đã save
        commentEventService.publishCommentCreated(taskComment);

        return taskCommentMapper.toResponse(taskComment);
    }


    @Override
    @Transactional
    public TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment) {

        Account actor = RequestContext.getAccount();

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, actor);

        if (requestDTO.getComment() != null) {
            taskComment.setComment(requestDTO.getComment());
        }

        taskComment = taskCommentRepository.saveAndFlush(taskComment);


        // Gửi event

        commentEventService.publishCommentUpdated(taskComment);


        return taskCommentMapper.toResponse(taskComment);
    }

    @Override
    @Transactional
    public TaskCommentResponseDTO deleteComment(String idComment) {

        Account actor = RequestContext.getAccount();

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, actor);

        taskComment.softDelete();

        commentAttachService.deleteCommentAttachByIdComment(taskComment.getId());

        taskComment = taskCommentRepository.save(taskComment);

        // Gửi event

        commentEventService.publishCommentDeleted(taskComment);


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
