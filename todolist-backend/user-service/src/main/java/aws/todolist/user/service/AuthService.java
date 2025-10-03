package aws.todolist.user.service;

//import com.sgu.backend.dto.request.auth.LoginRequestForm;
//import com.sgu.backend.dto.request.auth.UserRegistrationForm;
//import com.sgu.backend.dto.response.auth.AuthResponseDTO;

import aws.todolist.user.dto.account.AccountRedisDTO;
import aws.todolist.user.dto.auth.*;
import com.ec.user.dto.auth.*;
import aws.todolist.user.entity.Account;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
	
	Account activeAccount(String otp);
	
	boolean isUsernameExists(String username);
	
	AuthResponseDTO login(LoginRequestForm request);
	
	AuthResponseDTO staffLogin(LoginRequestForm request);
	
	AccountRedisDTO register(UserRegistrationForm userRegistrationForm);

	void sendOtpResetPassword(String username);
	
	Account resetPassword(String username, ResetPasswordForm form);
	
	Account updatePassword(UpdatePasswordForm form);
	
	void sendOtpUpdateEmail(String username);
	
	Account updateEmail(UpdateEmailForm form);
	AuthResponseDTO refreshToken(HttpServletRequest request);
	
}
