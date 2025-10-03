package com.ec.user.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordForm {
	
	private String otp;
	
	private String newPassword;

}
