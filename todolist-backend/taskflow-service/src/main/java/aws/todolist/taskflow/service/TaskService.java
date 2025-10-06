package aws.todolist.taskflow.service;

import aws.todolist.taskflow.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAllTask();

    Task getTaskById(String id);

    Task addTask(Task task);

    Task updateTask(String id, Task updateTask);

    Task removeTask(String id);
}
