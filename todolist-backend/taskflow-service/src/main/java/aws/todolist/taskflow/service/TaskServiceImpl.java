package aws.todolist.taskflow.service;


import aws.todolist.taskflow.dto.task.TaskCreateRequestDTO;
import aws.todolist.taskflow.dto.task.TaskDetailResponseDTO;
import aws.todolist.taskflow.dto.task.TaskResponseDTO;
import aws.todolist.taskflow.dto.task.TaskUpdatePriorityRequestDTO;
import aws.todolist.taskflow.entity.Section;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.TaskMapper;
import aws.todolist.taskflow.repository.SectionRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TaskMapper taskMapper;


    @Override
    public TaskDetailResponseDTO getTaskById(String idTask) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        return taskMapper.ResponseDetailDTO(task);
    }

    @Override
    public TaskResponseDTO addTask(TaskCreateRequestDTO requestDTO) {

        Task taskFather = null;

        Section section = sectionRepository.findByIdAndIsDeletedFalse(requestDTO.getSectionId());

        if (section == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Section doesn't exist or has been deleted");
        }

        if (requestDTO.getTaskFatherId() != null) {
            taskFather = taskRepository.findByIdAndIsDeletedFalse(requestDTO.getTaskFatherId());
            if (taskFather == null) {
                throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task Father doesn't exist or has been deleted");
            }
        }

        Task task = Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .priority(requestDTO.getPriority() != null ? requestDTO.getPriority() : null)
                .deadline(requestDTO.getDeadline())
                .startTime(requestDTO.getStartTime())
                .isPinned(requestDTO.getIsPinned() != null ? requestDTO.getIsPinned() : false)
                .isArchived(requestDTO.getIsArchived() != null ? requestDTO.getIsArchived() : false)
                .taskFather(taskFather)
                .section(section)
                .build();

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updatePriority(String idTask, TaskUpdatePriorityRequestDTO requestDTO) {

        Task task = taskRepository.findByIdAndIsDeletedFalse(idTask);

        if (task == null) {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Task doesn't exist or has been deleted");
        }

        task.setPriority(requestDTO.getPriority());

        task = taskRepository.save(task);

        return taskMapper.ResponseDTO(task);
    }

}
