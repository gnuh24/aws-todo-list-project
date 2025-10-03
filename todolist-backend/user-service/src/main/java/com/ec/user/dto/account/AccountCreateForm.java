package com.ec.user.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateForm {
	
	private String id;
	
	@NotBlank(message = "Email không được để trống !!")
	@Email(message = "Email phải đúng định dang !!")
	private String username;
	
	@NotBlank(message = "Mật khẩu không được để trống !!")
	@Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự !!")
	private String password;
	
}


