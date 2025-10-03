package com.ec.user.dto.address;

import com.ec.user.utils.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AddressResponseDTO {
	
	private String id;
	
	private String address;
	
	private Boolean isDefault ;
	
	private String fullName;
	
	private String phone;
	
}
