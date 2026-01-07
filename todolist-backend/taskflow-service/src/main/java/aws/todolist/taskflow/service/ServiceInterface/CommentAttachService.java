package aws.todolist.taskflow.service.ServiceInterface;

import aws.todolist.taskflow.dto.commentAttach.CommentAttachResponse;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.CommentAttach;

public interface CommentAttachService {
    CommentAttach addNewCommentAttach(String url, String idComment, Account account);

    CommentAttachResponse deleteCommentAttach(String url);

    void deleteCommentAttachByIdComment(String idComment);
}
