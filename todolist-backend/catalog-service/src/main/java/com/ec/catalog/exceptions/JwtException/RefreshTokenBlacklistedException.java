package com.ec.catalog.exceptions.JwtException;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenBlacklistedException extends AuthenticationException {
    public RefreshTokenBlacklistedException(String message) {
        super(message);
    }
}