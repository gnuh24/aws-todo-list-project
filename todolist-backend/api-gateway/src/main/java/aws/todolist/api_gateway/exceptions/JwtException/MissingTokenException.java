package aws.todolist.api_gateway.exceptions.JwtException;

public class MissingTokenException extends GenericJwtException {
	public MissingTokenException(String message) {
		super(message);
	}
}
