package aws.todolist.notification.exceptions.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.security.core.AuthenticationException;

public class HmacVerificationException  extends AuthenticationException {
    public HmacVerificationException(String message) {
        super(message);
    }
}
