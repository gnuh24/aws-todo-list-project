package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;
import aws.todolist.taskflow.entity.Account;

public interface TaskCommentService {

    TaskCommentResponseDTO addNewComment(TaskCommentRequestDTO requestDTO, String idTask, Account account);

    TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment, Account account);

    TaskCommentResponseDTO deleteComment(String idComment, Account account);

}
