package aws.todolist.project.service.ServiceEventKafka;

import aws.todolist.project.context.RequestContext;
import aws.todolist.project.dto.event.payload.SectionPayload;
import aws.todolist.project.entity.Account;
import aws.todolist.project.entity.Section;
import aws.todolist.project.enums.EventType;
import aws.todolist.project.mapper.ActorMapper;
import aws.todolist.project.mapper.SectionMapper;
import aws.todolist.project.messaging.kafka.producer.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionEventService {

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private GenericEventPublisher eventPublisher;

    @Autowired
    private ActorMapper actorMapper;

    // ===== PUBLIC USE-CASES =====

    public void publishSectionCreated(Section section) {
        publish(section, EventType.SECTION_CREATED);
    }

    public void publishSectionNameUpdated(Section section) {
        publish(section, EventType.SECTION_NAME_UPDATED);
    }

    public void publishSectionDeleted(Section section) {
        publish(section, EventType.SECTION_DELETED);
    }

    public void publishSectionMoved(Section section) {
        publish(section, EventType.SECTION_MOVED);
    }

    // ===== SHARED CORE =====

    private void publish(Section section, EventType eventType) {

        Account actor = RequestContext.getAccount();

        SectionPayload payload = sectionMapper.toPayload(
                section,
                actorMapper.toActorDto(actor),   // actor (nếu sau này cần)
                null    // receivers (nếu sau này cần)
        );

        eventPublisher.publishSectionEvent(
                section.getProject().getId(),
                payload,
                eventType
        );
    }
}
