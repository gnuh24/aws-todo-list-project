package aws.todolist.taskflow.service.ServiceEventKafka;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.event.payload.MemberPayload;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.MemberMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.service.ServiceInterface.AccountService;
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

        // 🚫 Remove actor khỏi receivers
        if (actor != null) {
            receivers.removeIf(email ->
                    email.equalsIgnoreCase(actor.getEmail())
            );
        }


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
