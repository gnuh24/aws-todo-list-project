package aws.todolist.taskflow.aspect;

import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect // class này chứa logic chặn/trước/sau method.
@Component
@RequiredArgsConstructor
public class ProjectRoleAspect {


    private final MemberRepository memberRepository;

    // Chạy trước method có @RequireProjectRole và có projectId là param
    @Before("@annotation(requireProjectRole)")
    public void checkProjectRole(JoinPoint joinPoint, RequireProjectRole requireProjectRole) {

        Object[] args = joinPoint.getArgs();
        String actualProjectId = (String) args[0];

        // Lấy user đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) authentication.getPrincipal();

        // Tìm member trong project
        Optional<Member> Optmember = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(account.getId(), actualProjectId);

        Member member;

        if (Optmember.isPresent()) {
            member = Optmember.get();
        } else {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "You are not a member of this project");
        }

        // Lấy danh sách role được phép
        List<Role> allowedRoles = Arrays.asList(requireProjectRole.value());

        if (!allowedRoles.contains(member.getRole())) {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED, "You do not have permission to access this resource");
        }
    }
}