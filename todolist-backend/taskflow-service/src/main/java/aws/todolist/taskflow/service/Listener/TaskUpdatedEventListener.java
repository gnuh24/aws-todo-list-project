package aws.todolist.taskflow.service.Listener;

import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.service.ServiceEventKafka.TaskEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class TaskUpdatedEventListener {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskEventService taskEventService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TaskUpdatedEvent event) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(event.taskId());

        taskEventService.publishTaskUpdated(task);
    }

    public record TaskUpdatedEvent(String taskId) {
    }
}
