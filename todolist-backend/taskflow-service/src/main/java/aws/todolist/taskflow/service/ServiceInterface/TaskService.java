package aws.todolist.taskflow.service.ServiceInterface;

import aws.todolist.taskflow.dto.task.*;

import java.util.List;

public interface TaskService {

    TaskDetailResponseDTO getTaskById(String idTask);

    List<TaskResponseDTO> getTaskUpComing();

    TaskResponseDTO addTask(String idProject, TaskCreateRequestDTO requestDTO);

    TaskResponseDTO updatePriority(String idTask, TaskUpdatePriorityRequestDTO requestDTO);

    TaskResponseDTO updateRelationship(String idTask, TaskUpdateRelationshipRequestDTO requestDTO);

    TaskResponseDTO updateStatus(String idTask, TaskUpdateStatusRequestDTO requestDTO);

    TaskResponseDTO assignTask(String idTask, String idProject, TaskAssignRequestDTO requestDTO);

    TaskResponseDTO assigneeTask(String idTask);

    TaskResponseDTO updateSectionForTask(String idTask, TaskUpdateSectionRequestDTO requestDTO);

    TaskResponseDTO updateTask(String idTask, TaskUpdateRequestDTO requestDTO);

    TaskResponseDTO archiveTask(String idTask, TaskArchivedRequestDTO requestDTO);

    TaskResponseDTO deleteTask(String idTask);

    TaskResponseDTO restore(String idTask, String idProject);
}
