package com.ec.user.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestForm {
	
	@NotBlank(message = "Username không được để trống !!")
//	@Email(message = "Username phải đúng định dạng !!")
	private String username;
	
	@NotBlank(message = "Bạn không thể để trống password")
	private String password;
	
}

