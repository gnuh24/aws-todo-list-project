package aws.todolist.taskflow.service.Listener;


import aws.todolist.taskflow.entity.TaskComment;
import aws.todolist.taskflow.repository.TaskCommentRepository;
import aws.todolist.taskflow.service.ServiceEventKafka.CommentEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CommentUpdatedEventListener {

    @Autowired
    private CommentEventService commentEventService;

    @Autowired
    private TaskCommentRepository repository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentAttachDeletedEvent event) {

        TaskComment taskComment = repository.findByIdAndIsDeletedFalse(event.commentId);

        commentEventService.publishCommentUpdated(taskComment);
    }

    public record CommentAttachDeletedEvent(String commentId) {
    }
}