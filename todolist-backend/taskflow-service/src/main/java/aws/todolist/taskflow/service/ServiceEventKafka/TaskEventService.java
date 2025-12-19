package aws.todolist.taskflow.service.ServiceEventKafka;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.event.payload.TaskPayload;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.TaskMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TaskEventService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private GenericEventPublisher eventPublisher;

    // =========================
    // CRUD
    // =========================
    public void publishTaskCreated(Task task) {
        publish(task, EventType.TASK_CREATED, false);
    }

    public void publishTaskUpdated(Task task) {
        publish(task, EventType.TASK_UPDATED, true);
    }

    public void publishTaskDeleted(Task task) {
        publish(task, EventType.TASK_DELETED, false);
    }

    public void publishTaskRestored(Task task) {
        publish(task, EventType.TASK_RESTORED, false);
    }

    public void publishTaskArchived(Task task) {
        publish(task, EventType.TASK_ARCHIVED, false);
    }

    // =========================
    // STATE / PROPERTY
    // =========================
    public void publishStatusUpdated(Task task) {
        publish(task, EventType.TASK_STATUS_UPDATED, true);
    }

    public void publishPriorityUpdated(Task task) {
        publish(task, EventType.TASK_PRIORITY_UPDATED, false);
    }

    public void publishSectionUpdated(Task task) {
        publish(task, EventType.TASK_SECTION_UPDATED, false);
    }

    // =========================
    // RELATION
    // =========================
    public void publishRelationshipUpdated(Task task) {
        publish(task, EventType.TASK_RELATIONSHIP_UPDATED, false);
    }

    // =========================
    // ASSIGNEE
    // =========================
    public void publishAssigned(Task task) {
        publish(task, EventType.TASK_ASSIGNED, true);
    }

    public void publishUnassigned(Task task) {
        publish(task, EventType.TASK_UNASSIGNED, true);
    }

    // =========================
    // QUARTZ (SYSTEM EVENT)
    // =========================
    public void publishDueSoon(Task task) {
        publish(task, EventType.TASK_DUE_SOON, true);
    }

    public void publishOverdue(Task task) {
        publish(task, EventType.TASK_OVERDUE, true);
    }

    // =========================
    // CORE
    // =========================
    private void publish(
            Task task,
            EventType eventType,
            boolean includeReceivers
    ) {
        Account actor = RequestContext.getAccount();

        List<String> receivers = includeReceivers
                ? resolveReceivers(task, actor)
                : null;

        TaskPayload payload = taskMapper.toPayload(
                task,
                actor != null ? actorMapper.toActorDto(actor) : null,
                receivers
        );

        eventPublisher.publishTaskEvent(
                task.getSection().getProject().getId(),
                payload,
                eventType
        );
    }

    /**
     * Receiver rule:
     * - Người tạo task
     * - Người được phân công
     * - Loại actor
     * - Không null
     * - Không trùng
     */
    private List<String> resolveReceivers(Task task, Account actor) {
        Set<String> receivers = new HashSet<>();

        if (task.getCreatedByAccount() != null) {
            receivers.add(task.getCreatedByAccount().getEmail());
        }

        if (task.getAccountAssign() != null) {
            receivers.add(task.getAccountAssign().getEmail());
        }

        if (actor != null) {
            receivers.remove(actor.getEmail());
        }

        return receivers.stream().toList();
    }
}
