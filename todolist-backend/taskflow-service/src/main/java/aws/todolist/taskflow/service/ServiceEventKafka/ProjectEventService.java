package aws.todolist.taskflow.service.ServiceEventKafka;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.event.payload.ProjectPayload;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.ProjectMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.taskflow.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectEventService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GenericEventPublisher eventPublisher;

    public void publishProjectCreated(Project project) {
        publish(project, EventType.PROJECT_CREATED);
    }

    public void publishProjectUpdated(Project project) {
        publish(project, EventType.PROJECT_UPDATED);
    }

    public void publishProjectDeleted(Project project) {
        publish(project, EventType.PROJECT_DELETED);
    }

    public void publishProjectArchived(Project project) {
        publish(project, EventType.PROJECT_ARCHIVED);
    }

    // ===== CORE =====

    private void publish(Project project, EventType type) {
        List<String> receivers =
                memberRepository.findEmailsInProject(project.getId());

        Account actor = RequestContext.getAccount();

        ProjectPayload payload = projectMapper.toPayload(
                project,
                actorMapper.toActorDto(actor),
                receivers
        );

        eventPublisher.publishProjectEvent(
                project.getId(),
                payload,
                type
        );
    }
}
