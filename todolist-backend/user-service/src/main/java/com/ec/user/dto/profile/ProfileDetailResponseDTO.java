package com.ec.user.dto.profile;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileDetailResponseDTO {
	
	private String id;
	
	private String email;
	
	private String phone;
	
	private String fullName;
	
	private LocalDate birthday;
	
	private String gender;
	
}
