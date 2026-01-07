package aws.todolist.user.filter;

import aws.todolist.user.context.RequestContext;
import aws.todolist.user.entity.Account;
import aws.todolist.user.service.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AccountContextFilter extends OncePerRequestFilter {
    @Autowired
    private AccountService accountService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String accountId = request.getHeader("X-User-Id");

            if (accountId != null && !accountId.isBlank()) {
                Account account = accountService.getAccountById(accountId);
                RequestContext.setAccount(account);
            }

            filterChain.doFilter(request, response);
        } finally {
            RequestContext.clear(); // 🚨 BẮT BUỘC
        }
    }
}
