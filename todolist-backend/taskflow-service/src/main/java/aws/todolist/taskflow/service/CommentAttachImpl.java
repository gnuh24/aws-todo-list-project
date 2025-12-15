package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.CommentAttach;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.mapper.CommentAttachMapper;
import aws.todolist.taskflow.repository.CommentAttachRepository;
import aws.todolist.taskflow.utils.TaskCommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
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
    private AwsService awsService;


    @Override
    @Transactional
    public CommentAttach addNewCommentAttach(String url, String idComment, Account account) throws URISyntaxException {

        TaskComment taskComment = taskCommentUtils.getCommentAndCheck(idComment, account);

        String newUrl = awsService.moveFileToAttach(url);

        CommentAttach commentAttach = CommentAttach.builder().attachmentUrl(newUrl).taskComment(taskComment).build();

        return commentAttachRepository.save(commentAttach);
    }

    @Override
    @Transactional
    public CommentAttachResponse deleteCommentAttach(String url) {

        System.err.println(url);

        CommentAttach commentAttach = commentAttachRepository.findByAttachmentUrl(url);

        if (commentAttach == null) {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Không tìm thấy comment attach này");
        }

        awsService.deleteFile(url);

        commentAttachRepository.delete(commentAttach);

        return commentAttachMapper.toResponse(commentAttach);
    }

    @Override
    @Transactional
    public void deleteCommentAttachByIdComment(String idComment) {

        // 2. Lấy tất cả CommentAttach liên quan
        List<CommentAttach> attachList = commentAttachRepository.findAllByTaskCommentId(idComment);

        if (attachList.isEmpty()) return;

        // 3. Xóa file trên S3
        attachList.forEach(attach -> {
            awsService.deleteFile(attach.getAttachmentUrl());
        });

        // 4. Xóa bản ghi DB
        commentAttachRepository.deleteAll(attachList);
        commentAttachRepository.flush();

    }
}
