package aws.todolist.taskflow.service.ServiceInterface;

import aws.todolist.taskflow.dto.taskComment.TaskCommentRequestDTO;
import aws.todolist.taskflow.dto.taskComment.TaskCommentResponseDTO;

public interface TaskCommentService {

    TaskCommentResponseDTO addNewComment(TaskCommentRequestDTO requestDTO, String idTask);

    TaskCommentResponseDTO updateComment(TaskCommentRequestDTO requestDTO, String idComment);

    TaskCommentResponseDTO deleteComment(String idComment);

}
