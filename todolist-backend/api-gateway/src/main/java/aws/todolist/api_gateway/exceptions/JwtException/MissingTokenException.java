package aws.todolist.api_gateway.exceptions.JwtException;

public class MissingTokenException extends AuthenticationException {
	public MissingTokenException(String message) {
		super(message);
	}
}
