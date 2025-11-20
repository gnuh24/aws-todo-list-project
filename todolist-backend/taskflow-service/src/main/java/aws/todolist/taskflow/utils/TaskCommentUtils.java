package aws.todolist.taskflow.utils;

import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskCommentUtils {

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    public TaskComment getCommentAndCheck(String idComment, Account account) {

        TaskComment taskComment = taskCommentRepository.findByIdAndIsDeletedFalse(idComment);

        if (taskComment == null) {
            System.out.println("Comment not found: id=" + idComment);
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Comment không tồn tại hoặc đã bị xóa");
        }

        if (!taskComment.getAccount().getId().equals(account.getId())) {
            System.out.println("Account not owner: commentId=" + idComment + ", accountId=" + account.getId());
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "Bạn không thể chỉnh sửa comment của người khác");
        }

        return taskComment;
    }
}
