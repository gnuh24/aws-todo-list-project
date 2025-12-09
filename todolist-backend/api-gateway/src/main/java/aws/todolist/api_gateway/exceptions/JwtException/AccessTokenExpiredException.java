package aws.todolist.api_gateway.exceptions.JwtException;

public class AccessTokenExpiredException extends AuthenticationException {
	public AccessTokenExpiredException(String message) {
		super(message);
	}
}
