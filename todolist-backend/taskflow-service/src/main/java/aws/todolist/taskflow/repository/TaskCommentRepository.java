package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskCommentRepository extends JpaRepository<TaskComment, String>, JpaSpecificationExecutor<TaskComment> {
}
