package com.ec.user.dto.address;

import com.ec.user.utils.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AddressUpdateForm {
	
	private String address;
	
	private String fullName;
	
	private String phone;
	
}
