package aws.todolist.auth.service;


import aws.todolist.auth.dto.account.AccountRedisDTO;
import aws.todolist.auth.dto.auth.*;
import aws.todolist.auth.entity.Account;

public interface AuthService {
	
	Account activeAccount(String email, String otp);
	
	boolean isEmailExists(String email);
	
	AuthResponseDTO login(LoginRequestForm request);
	
	AuthResponseDTO loginGoogle(String email, String name, String avatar);
	AuthResponseDTO staffLogin(LoginRequestForm request);
	AccountRedisDTO register(UserRegistrationForm userRegistrationForm);
	Account resetPassword(String username, ResetPasswordForm form);
	Account updatePassword(String accountId, UpdatePasswordForm form);
	Account updateEmail(String accountId,  UpdateEmailForm form);
	AuthResponseDTO refreshToken(String refreshToken);
	Account deleteAccount(String accountId, DeleteAccountForm form);
	
}
