package aws.todolist.project.service.ServiceEventKafka;

import aws.todolist.project.context.RequestContext;
import aws.todolist.project.dto.event.payload.ProjectPayload;
import aws.todolist.project.entity.Account;
import aws.todolist.project.entity.Project;
import aws.todolist.project.enums.EventType;
import aws.todolist.project.mapper.ActorMapper;
import aws.todolist.project.mapper.ProjectMapper;
import aws.todolist.project.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.project.repository.MemberRepository;
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
