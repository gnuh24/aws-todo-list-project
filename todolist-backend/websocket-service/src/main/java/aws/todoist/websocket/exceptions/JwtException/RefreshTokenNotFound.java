package aws.todoist.websocket.exceptions.JwtException;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenNotFound extends AuthenticationException {
	
	public RefreshTokenNotFound(String message) {
		super(message);
	}
}
