package com.ec.catalog.dto.address;

import lombok.Data;

@Data
public class AddressResponseDTO {
	
	private String id;
	
	private String address;
	
	private Boolean isDefault ;
	
	private String fullName;
	
	private String phone;
	
}
