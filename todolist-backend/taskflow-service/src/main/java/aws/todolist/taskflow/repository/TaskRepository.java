package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {

    Task findByIdAndIsDeletedFalse(String id);

    Task findByIdAndIsDeletedTrue(String id);
}
