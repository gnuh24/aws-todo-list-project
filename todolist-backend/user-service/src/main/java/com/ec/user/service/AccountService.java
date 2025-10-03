package com.ec.user.service;

//import com.sgu.backend.dto.request.account.*;
//import com.sgu.backend.dto.request.auth.UserRegistrationForm;
//import com.sgu.backend.entities.Account;
//import com.sgu.backend.entities.OTP;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import com.ec.user.dto.account.AccountCreateForm;
import com.ec.user.entity.Account;
import com.ec.user.entity.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
	
	Account getAccountById(String accountId);
	Account getAccountByUsername(String username);
	Account createAccount(AccountCreateForm accountCreateForm, Profile profile);
	Account activeAccount(String accountId);
	Account updatePassword(String username, String newPassword);
	Account updatePassword(Account account, String newPassword);
	
	Account updateUsername(Account account, String newEmail);

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