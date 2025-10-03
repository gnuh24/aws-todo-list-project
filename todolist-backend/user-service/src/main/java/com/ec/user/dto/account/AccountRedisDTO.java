package com.ec.user.dto.account;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class AccountRedisDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String username;
	
	private String password;
	
}
