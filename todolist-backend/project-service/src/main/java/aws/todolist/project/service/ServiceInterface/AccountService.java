package aws.todolist.project.service.ServiceInterface;

import aws.todolist.project.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account getAccountById(String accountId);
}