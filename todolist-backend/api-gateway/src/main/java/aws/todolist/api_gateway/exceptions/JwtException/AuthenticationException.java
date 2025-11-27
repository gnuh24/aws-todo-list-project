package aws.todolist.api_gateway.exceptions.JwtException;

public class AuthenticationException extends RuntimeException {
	public AuthenticationException(String message) {
		super(message);
	}
}
