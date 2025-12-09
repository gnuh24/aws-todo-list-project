package aws.todolist.api_gateway.exceptions.JwtException;

public class TokenExpiredException extends AuthenticationException {
	public TokenExpiredException(String message) {
		super(message);
	}
}
