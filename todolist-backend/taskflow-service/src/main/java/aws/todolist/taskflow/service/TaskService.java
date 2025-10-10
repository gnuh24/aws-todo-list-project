package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.task.*;
import aws.todolist.taskflow.entity.Account;

public interface TaskService {

    TaskDetailResponseDTO getTaskById(String idTask);

    TaskResponseDTO addTask(String idProject, TaskCreateRequestDTO requestDTO);

    TaskResponseDTO updatePriority(String idTask, TaskUpdatePriorityRequestDTO requestDTO);

    TaskResponseDTO updateRelationship(String idTask, TaskUpdateRelationshipRequestDTO requestDTO);

    TaskResponseDTO updateStatus(String idTask, TaskUpdateStatusRequestDTO requestDTO, Account account);

    TaskResponseDTO assignTask(String idTask, String idProject, TaskAssignRequestDTO requestDTO);

    TaskResponseDTO updateSectionForTask(String idTask, String idProject, TaskUpdateSectionRequestDTO requestDTO);
}
