package aws.todolist.taskflow.aspect;

import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.enums.StatusMember;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Aspect // class này chứa logic chặn/trước/sau method.
@Component
@RequiredArgsConstructor
public class ProjectRoleAspect {


    private final MemberRepository memberRepository;

    private final Integer DEFAULT_TTL = 30 * 60;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Chạy trước method có @RequireProjectRole và có projectId là param
    @Before("@annotation(requireProjectRole)")
    public void checkProjectRole(JoinPoint joinPoint, RequireProjectRole requireProjectRole) {

        Object[] args = joinPoint.getArgs();
        String actualProjectId = (String) args[0];

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String accountId = request.getHeader("X-User-Id");


        // TODO: Tìm trong redis nếu có thì dùng không thì tìm trong db

        String key = "user:" + accountId + ":project:" + actualProjectId + ":permissions";
        // Lấy từ Redis
        Object item = redisTemplate.opsForValue().get(key);

        Role role = null;

        if (item != null) {
            String value = item.toString();
            if (!"NONE".equals(value)) { // chỉ parse khi khác "NONE"
                role = Role.valueOf(value);
            }
        } else {
            Optional<Member> optMember = memberRepository.findFirstByAccountIdAndProjectIdAndStatusAndIsDeletedFalse(accountId, actualProjectId, StatusMember.ACCEPTED);
            if (optMember.isPresent()) {
                role = optMember.get().getRole();
                redisTemplate.opsForValue().set(key, role.name(), DEFAULT_TTL, TimeUnit.SECONDS);
            } else {
                // cache "NONE" thay cho role rỗng
                redisTemplate.opsForValue().set(key, "NONE", DEFAULT_TTL, TimeUnit.SECONDS);
            }
        }

        // kiểm tra quyền
        List<Role> allowedRoles = Arrays.asList(requireProjectRole.value());
        if (role == null || !allowedRoles.contains(role)) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED,
                    "Bạn không có quyền truy cập");
        }
    }
}