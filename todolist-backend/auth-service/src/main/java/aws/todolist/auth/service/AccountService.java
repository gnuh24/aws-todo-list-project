package aws.todolist.auth.service;

//import com.sgu.backend.dto.request.account.*;
//import com.sgu.backend.dto.request.auth.UserRegistrationForm;
//import com.sgu.backend.entities.Account;
//import com.sgu.backend.entities.OTP;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import aws.todolist.auth.dto.account.AccountCreateForm;
import aws.todolist.auth.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
	
	Account getAccountById(String accountId);
	Account getAccountByUsername(String email);
	Account saveAccount(AccountCreateForm accountCreateForm);
	Account saveAccount(Account account);
	Account activeAccount(String accountId);
	Account updatePassword(String email, String newPassword);
	Account updatePassword(Account account, String newPassword);
	
	Account updateEmail(Account account, String newEmail);

//    Page<Account> getAllAccounts(Pageable pageable, AccountFilterForm filterForm);
//
//    Account getAccountByEmail(String username);
//

//
//    Account updateStatusOfAccount(String accountId, Account.Status status);
//
//    Account updateRoleOfAccount(String accountId, Account.Role role);
//
//		Account resetPasswordOfAccount(OTP otp, AccountUpdateFormForResetPassword form);
}