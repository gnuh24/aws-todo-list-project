package aws.todolist.project.service.ServiceEventKafka;

import aws.todolist.project.context.RequestContext;
import aws.todolist.project.dto.event.payload.MemberPayload;
import aws.todolist.project.entity.Account;
import aws.todolist.project.entity.Member;
import aws.todolist.project.enums.EventType;
import aws.todolist.project.mapper.ActorMapper;
import aws.todolist.project.mapper.MemberMapper;
import aws.todolist.project.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.project.repository.MemberRepository;
import aws.todolist.project.service.ServiceInterface.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberEventService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GenericEventPublisher eventPublisher;


    // =========================
    // MEMBER INVITE
    // =========================
    public void publishMemberAdded(Member member) {

        Account actor = RequestContext.getAccount();

        MemberPayload payload = memberMapper.toPayload(
                member,
                actorMapper.toActorDto(actor),
                List.of(member.getAccount().getEmail()) // chỉ gửi cho người được mời
        );

        eventPublisher.publishMemberEvent(
                member.getProject().getId(),
                payload,
                EventType.PROJECT_MEMBER_ADDED
        );
    }

    // =========================
    // ROLE UPDATED
    // =========================
    public void publishRoleUpdated(Member member) {
        publishToProjectMembers(member, EventType.PROJECT_MEMBER_ROLE_UPDATED);
    }

    // =========================
    // MEMBER REMOVED
    // =========================
    public void publishMemberRemoved(Member member) {
        publishToProjectMembers(member, EventType.PROJECT_MEMBER_REMOVED);
    }

    // =========================
    // INVITE RESPONSE
    // =========================
    public void publishInviteAccepted(Member member) {
        publishToProjectMembers(member, EventType.PROJECT_MEMBER_ACCEPTED);
    }

    public void publishInviteDeclined(Member member) {
        publishToProjectMembers(member, EventType.PROJECT_MEMBER_DECLINED);
    }

    // =========================
    // CORE
    // =========================
    private void publishToProjectMembers(Member member, EventType type) {

        Account actor = RequestContext.getAccount();

        List<String> receivers = memberRepository.findEmailsInProject(member.getProject().getId());

        MemberPayload payload = memberMapper.toPayload(
                member,
                actor != null ? actorMapper.toActorDto(actor) : null,
                receivers
        );

        eventPublisher.publishMemberEvent(
                member.getProject().getId(),
                payload,
                type
        );
    }
}
