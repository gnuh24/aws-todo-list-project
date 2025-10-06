package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {

    List<Task> findByIsDeletedFalseOrderByUpdatedAtDesc();
}
