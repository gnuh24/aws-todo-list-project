package aws.todolist.taskflow.service.ServiceEventKafka;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.event.payload.CommentPayload;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.TaskCommentMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommentEventService {

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private GenericEventPublisher eventPublisher;

    // ===== PUBLIC API (Use-cases) =====

    public void publishCommentCreated(
            TaskComment comment
    ) {
        publish(
                comment,
                EventType.COMMENT_CREATED
        );
    }

    public void publishCommentUpdated(
            TaskComment comment
    ) {
        publish(
                comment,
                EventType.COMMENT_UPDATED
        );
    }

    public void publishCommentDeleted(
            TaskComment comment
    ) {
        publish(
                comment,
                EventType.COMMENT_DELETED
        );
    }

    // ===== CORE (shared logic) =====

    private void publish(
            TaskComment comment,
            EventType eventType
    ) {

        Account actor = RequestContext.getAccount();

        Task task = comment.getTask();

        CommentPayload payload = taskCommentMapper.toPayload(
                comment,
                actorMapper.toActorDto(actor),
                receiversFromTask(task, actor)
        );

        eventPublisher.publishCommentEvent(
                task.getSection().getProject().getId(),
                payload,
                eventType
        );
    }

    // ===== RECEIVER RULE =====

    private List<String> receiversFromTask(Task task, Account actor) {
        Set<String> receivers = new HashSet<>();

        // 1. Người tạo task
        if (task.getCreatedByAccount() != null) {
            receivers.add(task.getCreatedByAccount().getEmail());
        }

        // 2. Người đã comment
        task.getTaskComments().forEach(tc -> {
            if (tc.getAccount() != null) {
                receivers.add(tc.getAccount().getEmail());
            }
        });

        // 3. Người được giao task
        if (task.getAccountAssign() != null) {
            receivers.add(task.getAccountAssign().getEmail());
        }

        // 4. Loại actor
        if (actor != null) {
            receivers.remove(actor.getEmail());
        }

        return receivers.stream().toList();
    }

}
