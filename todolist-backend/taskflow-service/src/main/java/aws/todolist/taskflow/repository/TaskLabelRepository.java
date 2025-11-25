package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TaskLabelRepository extends JpaRepository<TaskLabel, String> {

    List<TaskLabel> findAllByTaskId(String taskId);
}
