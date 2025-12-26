package aws.todolist.notification.service;

import aws.todolist.notification.entity.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {

    Account getAccountById(String accountId);

    Account getAccountByEmail(String email);
}