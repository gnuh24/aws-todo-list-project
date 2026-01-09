package aws.todolist.taskflow.filter;

import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.service.ServiceInterface.AccountService;
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
            // ===== Account =====
            String accountId = request.getHeader("X-User-Id");
            if (accountId != null && !accountId.isBlank()) {
                Account account = accountService.getAccountById(accountId);
                RequestContext.setAccount(account);
            }

            // ===== Client Id =====
            String clientId = request.getHeader("X-Client-Id");
            if (clientId != null && !clientId.isBlank()) {
                RequestContext.setClientId(clientId);
            }

            filterChain.doFilter(request, response);

        } finally {
            RequestContext.clear(); // 🚨 BẮT BUỘC
        }
    }
}
