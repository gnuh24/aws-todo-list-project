package com.ec.user.dto.auth;

import lombok.Data;

@Data
public class UpdatePasswordForm {
	
	private String oldPassword;
	
	private String newPassword;

}
