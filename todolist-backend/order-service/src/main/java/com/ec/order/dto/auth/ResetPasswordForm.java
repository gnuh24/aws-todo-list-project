package com.ec.catalog.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordForm {
	
	private String otp;
	
	private String newPassword;

}
