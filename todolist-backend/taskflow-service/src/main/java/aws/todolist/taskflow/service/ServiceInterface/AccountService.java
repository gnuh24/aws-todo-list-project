package aws.todolist.taskflow.service.ServiceInterface;

import aws.todolist.taskflow.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account getAccountById(String accountId);
}