package aws.todolist.taskflow.service;


import aws.todolist.taskflow.entity.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Override
    public List<Task> getAllTask() {
        return List.of();
    }

    @Override
    public Task getTaskById(String id) {
        return null;
    }

    @Override
    public Task addTask(Task task) {
        return null;
    }

    @Override
    public Task updateTask(String id, Task updateTask) {
        return null;
    }

    @Override
    public Task removeTask(String id) {
        return null;
    }
}
