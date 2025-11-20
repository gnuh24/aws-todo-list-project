package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.CommentAttach;

import java.net.URISyntaxException;

public interface CommentAttachService {
    CommentAttach addNewCommentAttach(String url, String idComment, Account account) throws URISyntaxException;

    CommentAttachResponse deleteCommentAttach(String url);

    void deleteCommentAttachByIdComment(String idComment);
}
