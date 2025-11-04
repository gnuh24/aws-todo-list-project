package aws.todolist.auth.service;


import aws.todolist.auth.dto.account.AccountRedisDTO;
import aws.todolist.auth.dto.auth.*;
import aws.todolist.auth.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
	
	Account activeAccount(String otp);
	
	boolean isEmailExists(String email);
	
	AuthResponseDTO login(LoginRequestForm request);
	
	AuthResponseDTO loginGoogle(String email, String name, String avatar);
	
	AuthResponseDTO staffLogin(LoginRequestForm request);
	
	AccountRedisDTO register(UserRegistrationForm userRegistrationForm);

	void sendOtpResetPassword(String username);
	
	Account resetPassword(String username, ResetPasswordForm form);
	
	Account updatePassword(UpdatePasswordForm form);
	
	void sendOtpUpdateEmail(String username);
	
	Account updateEmail(UpdateEmailForm form);
	AuthResponseDTO refreshToken(String refreshToken);
}
