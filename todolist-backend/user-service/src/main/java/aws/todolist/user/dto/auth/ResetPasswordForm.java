package aws.todolist.user.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordForm {
	
	private String otp;
	
	private String newPassword;

}
