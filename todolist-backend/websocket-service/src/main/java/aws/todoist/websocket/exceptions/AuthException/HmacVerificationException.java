package aws.todoist.websocket.exceptions.AuthException;

import org.springframework.security.core.AuthenticationException;

public class HmacVerificationException  extends AuthenticationException {
    public HmacVerificationException(String message) {
        super(message);
    }
}
