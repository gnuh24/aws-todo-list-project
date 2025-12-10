package aws.todolist.api_gateway.exceptions.JwtException;

public class GenericJwtException extends RuntimeException {
	public GenericJwtException(String message) {
		super(message);
	}
}
