package com.ec.catalog.exceptions.AuthException;

import org.springframework.security.core.AuthenticationException;

public class StepUpAuthenticationException extends AuthenticationException {
    public StepUpAuthenticationException(String msg) {
        super(msg);
    }
}
