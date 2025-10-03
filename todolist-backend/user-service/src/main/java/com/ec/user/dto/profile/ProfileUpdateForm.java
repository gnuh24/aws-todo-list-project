package com.ec.user.dto.profile;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateForm {
	
	private String phone;
	
	private String fullName;
	
	private LocalDate birthday;
	
	private String gender;
	
}
