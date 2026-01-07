package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.CommentAttach;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.mapper.CommentAttachMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.taskflow.repository.CommentAttachRepository;
import aws.todolist.taskflow.service.Listener.CommentUpdatedEventListener;
import aws.todolist.taskflow.service.ServiceInterface.CommentAttachService;
import aws.todolist.taskflow.utils.TaskCommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Service
public class CommentAttachImpl implements CommentAttachService {

    @Autowired
    private CommentAttachRepository commentAttachRepository;

    @Autowired
    private TaskCommentUtils taskCommentUtils;

    @Autowired
    private CommentAttachMapper commentAttachMapper;

    @Autowired
    private GenericEventPublisher genericEventPublisher;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    @Transactional
    public CommentAttach addNewCommentAttach(String url, String idComment, Account account) {

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, account);

        CommentAttach commentAttach = CommentAttach.builder().attachmentUrl(url).taskComment(taskComment).build();

        return commentAttachRepository.save(commentAttach);
    }

    @Override
    @Transactional
    public CommentAttachResponse deleteCommentAttach(String url) {

        CommentAttach commentAttach = commentAttachRepository.findByAttachmentUrl(url);

        if (commentAttach == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Không tìm thấy comment attach này");
        }

        commentAttachRepository.delete(commentAttach);

        CommentAttachResponse commentAttachResponse = commentAttachMapper.toResponse(commentAttach);

        genericEventPublisher.publishCommentAttachEvent(commentAttach.getId(), commentAttachResponse, EventType.COMMENT_ATTACH_DELETED);

        // Gửi event cho comment
        applicationEventPublisher.publishEvent(
                new CommentUpdatedEventListener.CommentAttachDeletedEvent(commentAttach.getTaskComment().getId())
        );

        return commentAttachResponse;
    }

    @Override
    @Transactional
    public void deleteCommentAttachByIdComment(String idComment) {

        // 1. Lấy tất cả CommentAttach liên quan
        List<CommentAttach> attachList = commentAttachRepository.findAllByTaskCommentId(idComment);

        if (attachList.isEmpty()) return;

        // 2. Gửi event của từng comment attach để xóa ảnh

        for (CommentAttach attach : attachList) {
            genericEventPublisher.publishCommentAttachEvent(attach.getId(), commentAttachMapper.toResponse(attach), EventType.COMMENT_ATTACH_DELETED);
        }

        // 4. Xóa bản ghi DB
        commentAttachRepository.deleteAll(attachList);
        commentAttachRepository.flush();

    }
}
