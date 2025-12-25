package aws.todolist.auth.dto.auth;

import lombok.Data;

@Data
public class UpdatePasswordForm {
	
	private String oldPassword;
	
	private String newPassword;
	
	private Integer totp;

}
