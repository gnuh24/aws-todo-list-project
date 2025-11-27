package aws.todolist.api_gateway.exceptions.JwtException;


public class InvalidTokenTypeException extends AuthenticationException {
	public InvalidTokenTypeException(String message) {
		super(message);
	}
}
