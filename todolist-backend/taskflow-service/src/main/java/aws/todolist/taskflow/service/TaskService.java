package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.task.TaskCreateRequestDTO;
import aws.todolist.taskflow.dto.task.TaskDetailResponseDTO;
import aws.todolist.taskflow.dto.task.TaskResponseDTO;
import aws.todolist.taskflow.dto.task.TaskUpdatePriorityRequestDTO;

public interface TaskService {

    TaskDetailResponseDTO getTaskById(String idTask);

    TaskResponseDTO addTask(TaskCreateRequestDTO requestDTO);

    TaskResponseDTO updatePriority(String idTask, TaskUpdatePriorityRequestDTO requestDTO);
}
