package aws.todolist.api_gateway.exceptions.JwtException;

public class TokenExpiredException extends GenericJwtException {
	public TokenExpiredException(String message) {
		super(message);
	}
}
