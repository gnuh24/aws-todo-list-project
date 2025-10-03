package com.ec.user.exceptions.JwtException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class RefreshTokenNotFound extends AuthenticationException {
	
	public RefreshTokenNotFound(String message) {
		super(message);
	}
}
