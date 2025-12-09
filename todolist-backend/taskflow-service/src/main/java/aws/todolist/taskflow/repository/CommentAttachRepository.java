package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.CommentAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentAttachRepository extends JpaRepository<CommentAttach, String>, JpaSpecificationExecutor<CommentAttach> {
    CommentAttach findByAttachmentUrl(String attachmentUrl);

    List<CommentAttach> findAllByTaskCommentId(String taskCommentId);

}
