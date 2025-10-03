package com.ec.user.exceptions.JwtException;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenExpiredException extends AuthenticationException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}