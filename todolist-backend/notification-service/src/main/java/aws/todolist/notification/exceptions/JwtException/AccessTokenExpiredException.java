package aws.todolist.notification.exceptions.JwtException;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenExpiredException extends AuthenticationException {
	public AccessTokenExpiredException(String message) {
		super(message);
	}
}
