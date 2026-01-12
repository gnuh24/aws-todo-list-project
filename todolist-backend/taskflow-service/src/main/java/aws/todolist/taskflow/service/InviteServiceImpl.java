package aws.todolist.taskflow.service;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.exceptions.errorCode.BusinessErrorCode;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import aws.todolist.taskflow.service.ServiceEventKafka.MemberEventService;
import aws.todolist.taskflow.service.ServiceInterface.InviteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class InviteServiceImpl implements InviteService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberEventService memberEventService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${invite.path}")
    private String invitePath;

    @Value("${invite.ttl-minutes}")
    private long inviteTtlMinutes;


    @Override
    public String createInviteLink(String projectId) {

        String projectKey = "invite:project:" + projectId;

        String existingToken = (String) redisTemplate.opsForValue().get(projectKey);

        if (existingToken != null) {
            return frontendBaseUrl + invitePath + "/" + existingToken;
        }

        String token = UUID.randomUUID().toString();

        String tokenKey = "invite:token:" + token;

        redisTemplate.opsForValue().set(
                tokenKey,
                projectId,
                inviteTtlMinutes,
                TimeUnit.MINUTES
        );

        redisTemplate.opsForValue().set(
                projectKey,
                token,
                inviteTtlMinutes,
                TimeUnit.MINUTES
        );

        return frontendBaseUrl + invitePath + "/" + token;
    }


    @Override
    @Transactional
    public void verifyInvite(String token) {

        Account account = RequestContext.getAccount();

        String tokenKey = "invite:token:" + token;

        // 1. Lấy projectId
        String projectId = (String) redisTemplate.opsForValue().get(tokenKey);

        if (projectId == null) {
            throw new BadRequestException(
                    SystemErrorCode.API_BAD_REQUEST,
                    "Link mời không hợp lệ hoặc đã hết hạn"
            );
        }

        // 2. Check member đã tồn tại chưa
        boolean exists = memberRepository
                .findFirstByAccountIdAndProjectIdAndIsDeletedFalse(
                        account.getId(), projectId
                )
                .isPresent();

        if (exists) {
            throw new BadRequestException(
                    SystemErrorCode.API_BAD_REQUEST,
                    "Bạn đã là thành viên của project"
            );
        }

        // 3. Check project
        Project project = projectRepository
                .findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BusinessErrorCode.TASKFLOW_NOT_FOUND,
                        "Project không tồn tại"
                ));

        // 4. Create member
        Member member = Member.builder()
                .account(account)
                .project(project)
                .role(Role.MEMBER)
                .status(StatusMember.PENDING)
                .build();

        memberRepository.save(member);

        // 5. Notify
        memberEventService.publishMemberAdded(member);
    }

}
