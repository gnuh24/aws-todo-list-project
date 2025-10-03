package com.ec.catalog.dto.auth;

import lombok.Data;

@Data
public class UpdatePasswordForm {
	
	private String oldPassword;
	
	private String newPassword;

}
