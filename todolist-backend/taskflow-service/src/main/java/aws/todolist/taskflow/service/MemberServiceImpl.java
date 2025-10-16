package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRoleRequestDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateStatusRequestDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.mapper.MemberMapper;
import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.messaging.kafka.producer.KafkaNotificationProducer;
import aws.todolist.taskflow.repository.AccountRepository;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private ProjectRepository projectRepository;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private KafkaNotificationProducer kafkaNotificationProducer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<MemberResponseDTO> getAllMember(String idProject) {
        List<Member> members = memberRepository.findAllByProjectId(idProject);

        return memberMapper.ResponseDTOList(members);
    }

    @Transactional
    @Override
    public MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO) {

        // Kiểm tra xem member của account và project đã được tạo chưa
        Optional<Member> OptMember = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(requestDTO.getIdAccount(), idProject);

        if (OptMember.isPresent()) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "This account has already been added to this project.");
        }

        // Lấy thông tin chi tiết của project và account

        Optional<Project> OptProject = projectRepository.findByIdAndIsDeletedFalse(idProject);

        Optional<Account> OptAccount = accountRepository.findByIdAndIsDeletedFalse(requestDTO.getIdAccount());

        Project project;

        Account account;

        if (OptProject.isPresent()) {
            project = OptProject.get();
        } else {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Project doesn't exist.");
        }

        if (OptAccount.isPresent()) {
            account = OptAccount.get();
        } else {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "Account doesn't exist.");
        }

        // Kiểm tra người dùng có phân quyền làm owner không

        if (requestDTO.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot assign OWNER role to a member.");
        }

        Member member = Member.builder().account(account).project(project).role(requestDTO.getRole()).status(StatusMember.PENDING).build();

        Member member_saved = memberRepository.saveAndFlush(member);
	    
	    // =============================
	    // 🔔 GỬI KAFKA NOTIFICATION
	    // =============================
	    
	    NotificationMessage message = NotificationMessage.builder()
		.receiverId(account.getId())                          // Người nhận: account vừa được thêm
		.actorId(getCurrentActorId()) // Người thực hiện (lấy từ Auth)
		.projectId(project.getId())                            // Dự án liên quan
		.type(NotificationType.PROJECT_MEMBER_ADDED)           // Loại thông báo
		.title("Bạn đã được thêm vào dự án mới!")              // Tiêu đề thông báo
		.content(String.format(
		    "Bạn đã được thêm vào dự án '%s' với vai trò %s.",
		    project.getName(),
		    requestDTO.getRole().name()
		))
		.build();
	    
	    kafkaNotificationProducer.sendProjectMemberAdded(message);
	    
	    // =============================

        return memberMapper.ResponseDTO(member_saved);
    }

    @Override
    @Transactional
    public MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRoleRequestDTO requestDTO) {
        Optional<Member> OptMember = memberRepository.findFirstByIdAndIsDeletedFalse(idMember);

        Member member;

        if (OptMember.isPresent()) {
            member = OptMember.get();
        } else {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "This member doesn't exist");
        }

        if (member.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot change role: this member is an OWNER.");
        }

        if (requestDTO.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot assign OWNER role to a member.");
        }

        member.setRole(requestDTO.getRole());

        Member member_saved = memberRepository.saveAndFlush(member);

        // Sau khi lưu xong thì xóa cache

        String key = "user:" + member.getAccount().getId() + ":project:" + member.getProject().getId() + ":permissions";

        redisTemplate.delete(key);

	    
	    // =============================
	    // 🔔 Gửi Kafka Notification
	    // =============================
	    
	    try {
		    NotificationMessage message = NotificationMessage.builder()
			.receiverId(member_saved.getAccount().getId())              // người được cập nhật quyền
			.actorId(getCurrentActorId()) // Người thực hiện (lấy từ Auth)
			.projectId(member_saved.getProject().getId())
			.type(NotificationType.PROJECT_MEMBER_ROLE_UPDATED)
			.title("Vai trò của bạn trong dự án đã được cập nhật")
			.content("Vai trò mới của bạn trong dự án \""
			    + member_saved.getProject().getName()
			    + "\" là: " + member_saved.getRole().name())
			.build();
		    
		    kafkaNotificationProducer.sendProjectMemberRoleUpdated(message);
		    
		    System.out.println("📤 [Kafka] Sent PROJECT_MEMBER_ROLE_UPDATED for member " + member_saved.getAccount().getEmail());
	    } catch (Exception e) {
		    System.err.println("❌ Gửi notification PROJECT_MEMBER_ROLE_UPDATED thất bại: " + e.getMessage());
	    }
	
        return memberMapper.ResponseDTO(member_saved);
    }

    @Override
    @Transactional
    public MemberResponseDTO deleteMember(String idMember) {
        Optional<Member> OptMember = memberRepository.findFirstByIdAndIsDeletedFalse(idMember);

        Member member;

        if (OptMember.isPresent()) {
            member = OptMember.get();
        } else {
            throw new ResourceNotFoundException(SystemErrorCode.SYS_OBJECT_NOT_FOUND, "This member doesn't exist");
        }

        if (member.getRole() == Role.OWNER) {
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "Cannot delete member: this member is an OWNER.");
        }

        member.softDelete();

        Member member_saved = memberRepository.saveAndFlush(member);

        // Sau khi lưu xong thì xóa cache

        String key = "user:" + member.getAccount().getId() + ":project:" + member.getProject().getId() + ":permissions";

        redisTemplate.delete(key);

        return memberMapper.ResponseDTO(member_saved);
    }

    @Override
    public MemberResponseDTO responseRequestMember(String idProject, MemberUpdateStatusRequestDTO requestDTO, Account account) {

        Optional<Member> optMember = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(account.getId(), idProject);

        if (optMember.isEmpty()) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED,
                    "You are not invited to this project.");
        }

        Member member = optMember.get();

        if (member.getStatus() != StatusMember.PENDING) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED,
                    "You have already responded to this request.");
        }


        // Kiểm tra thời hạn xem còn cập nhật trạng thái được không
        LocalDateTime expireTime = member.getCreatedAt().plusDays(5);
        if (LocalDateTime.now().isAfter(expireTime)) {
            member.softDelete();
            memberRepository.save(member);
            throw new BadRequestException(SystemErrorCode.API_BAD_REQUEST, "This task invitation has expired. Please contact the admin to be invited again.");
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

        return memberMapper.ResponseDTO(member);
    }
	
	private String getCurrentActorId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("User not authenticated");
		}
		
		Object principal = authentication.getPrincipal();
		if (principal instanceof Account account) {
			return account.getId();
		}
		
		throw new RuntimeException("Invalid principal type");
	}
}
