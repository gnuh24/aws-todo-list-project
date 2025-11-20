package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskCommentRepository extends JpaRepository<TaskComment, String>, JpaSpecificationExecutor<TaskComment> {
    @Query("SELECT t FROM TaskComment t WHERE t.id = :id AND t.isDeleted = false")
    TaskComment findByIdAndIsDeletedFalse(@Param("id") String idComment);
}
