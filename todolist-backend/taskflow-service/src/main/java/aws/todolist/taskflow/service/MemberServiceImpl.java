package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.event.payload.MemberPayload;
import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRoleRequestDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateStatusRequestDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.enums.EventType;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.ActorMapper;
import aws.todolist.taskflow.mapper.MemberMapper;
import aws.todolist.taskflow.messaging.kafka.producer.GenericEventPublisher;
import aws.todolist.taskflow.repository.AccountRepository;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import aws.todolist.taskflow.utils.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationUtils notificationUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GenericEventPublisher genericEventPublisher;

    @Autowired
    private AccountService accountService;

    @Override
    public List<MemberResponseDTO> getAllMember(String idProject) {
        List<Member> members = memberRepository.findAllByProjectId(idProject);

        return memberMapper.toResponseList(members);
    }

    @Transactional
    @Override
    public MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO, String accountId) {

        // Kiểm tra xem member của account và project đã được tạo chưa
        Optional<Member> OptMember = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(requestDTO.getIdAccount(), idProject);

        if (OptMember.isPresent()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Tài khoản này đã được thêm vào dự án");
        }

        // Lấy thông tin chi tiết của project và account

        Optional<Project> OptProject = projectRepository.findByIdAndIsDeletedFalse(idProject);

        Optional<Account> OptAccount = accountRepository.findByIdAndIsDeletedFalse(requestDTO.getIdAccount());

        Project project;

        Account accountReceiveInvite;

        if (OptProject.isPresent()) {
            project = OptProject.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Dự án không tồn tại");
        }

        if (OptAccount.isPresent()) {
            accountReceiveInvite = OptAccount.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Tài khoản không tồn tại.");
        }

        // Kiểm tra người dùng có phân quyền làm owner không

        if (requestDTO.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Không thể phân quyền OWNER cho các thành viên khác.");
        }

        Member member = Member.builder().account(accountReceiveInvite).project(project).role(requestDTO.getRole()).status(StatusMember.PENDING).build();

        Member member_saved = memberRepository.saveAndFlush(member);

        // =============================
        // 🔔 GỬI KAFKA NOTIFICATION
        // =============================
        Account actor = accountService.getAccountById(accountId);

        MemberPayload payload = memberMapper.toPayload(member_saved, actorMapper.toActorDto(actor), List.of(accountReceiveInvite.getId()));
        genericEventPublisher.publishMemberEvent(member_saved.getProject().getId(), payload, EventType.PROJECT_MEMBER_ADDED);

        // =============================

        return memberMapper.toResponse(member_saved);
    }

    @Override
    @Transactional
    public MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRoleRequestDTO requestDTO, String accountId) {
        Optional<Member> OptMember = memberRepository.findFirstByIdAndIsDeletedFalse(idMember);

        Member member = null;

        if (OptMember.isPresent()) {
            member = OptMember.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Member không tồn tại");
        }

        if (member.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Không thể phân quyền OWNER cho người dùng khác");
        }

        if (requestDTO.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Không thể phân quyền OWNER cho người dùng khác");
        }

        member.setRole(requestDTO.getRole());

        Member member_saved = memberRepository.saveAndFlush(member);

        // Sau khi lưu xong thì xóa cache

        String key = "user:" + member.getAccount().getId() + ":project:" + member.getProject().getId() + ":permissions";

        redisTemplate.delete(key);


        // =============================
        // 🔔 Gửi Kafka Notification
        // =============================

        Account actor = accountService.getAccountById(accountId);
        List<String> listReceiver = memberRepository.findAccountIdsByProjectId(member_saved.getProject().getId());
        MemberPayload payload = memberMapper.toPayload(member_saved, actorMapper.toActorDto(actor), listReceiver);
        genericEventPublisher.publishMemberEvent(member_saved.getProject().getId(), payload, EventType.PROJECT_MEMBER_ROLE_UPDATED);

        return memberMapper.toResponse(member_saved);
    }

    @Override
    @Transactional
    public MemberResponseDTO deleteMember(String idMember) {
        Optional<Member> OptMember = memberRepository.findFirstByIdAndIsDeletedFalse(idMember);

        Member member;

        if (OptMember.isPresent()) {
            member = OptMember.get();
        } else {
            throw new ResourceNotFoundException(BusinessErrorCode.TASKFLOW_NOT_FOUND, "Member không tồn tại");
        }

        if (member.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Không thể xóa người dùng là OWNER");
        }

        member.softDelete();

        Member member_saved = memberRepository.saveAndFlush(member);

        // Sau khi lưu xong thì xóa cache

        String key = "user:" + member.getAccount().getId() + ":project:" + member.getProject().getId() + ":permissions";

        redisTemplate.delete(key);

        // Gửi kafka cho event

        MemberPayload payload = memberMapper.toPayload(member_saved, null, null);
        genericEventPublisher.publishMemberEvent(member_saved.getProject().getId(), payload, EventType.PROJECT_MEMBER_REMOVED);

        return memberMapper.toResponse(member_saved);
    }

    @Override
    public MemberResponseDTO responseRequestMember(String idProject, MemberUpdateStatusRequestDTO requestDTO, Account account) {

        Optional<Member> optMember = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(account.getId(), idProject);

        if (optMember.isEmpty()) {
            throw new ForbiddenException(BusinessErrorCode.TASKFLOW_ACCESS_DENIED,
                    "Bạn không được mời vào project.");
        }

        Member member = optMember.get();

        if (member.getStatus() != StatusMember.PENDING) {
            throw new ForbiddenException(BusinessErrorCode.TASKFLOW_ACCESS_DENIED,
                    "Bạn đã phản hồi lời mời rồi.");
        }


        // Kiểm tra thời hạn xem còn cập nhật trạng thái được không
        LocalDateTime expireTime = member.getCreatedAt().plusDays(5);
        if (LocalDateTime.now().isAfter(expireTime)) {
            member.softDelete();
            memberRepository.save(member);
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Lời mời đã hết hạn. Vui lòng liên hệ với chủ project");
        }

        if (member.getStatus() == StatusMember.PENDING) {
            member.setStatus(requestDTO.getStatus());
            if (member.getStatus() == StatusMember.DECLINED) {
                member.softDelete();
            }
        } else {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Only update the status for members who are pending.");
        }

        member = memberRepository.save(member);

        String key = "user:" + member.getAccount().getId() + ":project:" + member.getProject().getId() + ":permissions";

        redisTemplate.delete(key);

        // =============================
        // 🔔 Gửi Kafka Notification
        // =============================

        List<String> listReceiver = memberRepository.findAccountIdsByProjectId(idProject);
        MemberPayload payload = memberMapper.toPayload(member, actorMapper.toActorDto(account), listReceiver);
        if (member.getStatus() == StatusMember.ACCEPTED) {
            genericEventPublisher.publishMemberEvent(idProject, payload, EventType.PROJECT_MEMBER_ACCEPTED);

        } else {
            genericEventPublisher.publishMemberEvent(idProject, payload, EventType.PROJECT_MEMBER_DECLINED);

        }

        return memberMapper.toResponse(member);
    }

}
