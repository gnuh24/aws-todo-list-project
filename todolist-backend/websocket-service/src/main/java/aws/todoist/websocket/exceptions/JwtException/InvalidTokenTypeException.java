package aws.todoist.websocket.exceptions.JwtException;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenTypeException extends AuthenticationException {
	public InvalidTokenTypeException(String message) {
		super(message);
	}
}
