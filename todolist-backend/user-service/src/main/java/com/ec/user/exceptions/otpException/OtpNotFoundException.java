package com.ec.user.exceptions.otpException;

import org.springframework.security.core.AuthenticationException;

public class OtpNotFoundException extends AuthenticationException {
    public OtpNotFoundException(String msg) {
        super(msg);
    }
}
