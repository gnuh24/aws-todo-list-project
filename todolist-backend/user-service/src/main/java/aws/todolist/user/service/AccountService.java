package aws.todolist.user.service;

//import com.sgu.backend.dto.request.account.*;
//import com.sgu.backend.dto.request.auth.UserRegistrationForm;
//import com.sgu.backend.entities.Account;
//import com.sgu.backend.entities.OTP;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import aws.todolist.user.dto.account.AccountUpdateForm;
import aws.todolist.user.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
	
	Account getAccountById(String accountId);
	Account getAccountByUsername(String username);
	Account updateAccount(Account account, AccountUpdateForm form);
//    Page<Account> getAllAccounts(Pageable pageable, AccountFilterForm filterForm);
//
//    Account getAccountByEmail(String username);
//

}