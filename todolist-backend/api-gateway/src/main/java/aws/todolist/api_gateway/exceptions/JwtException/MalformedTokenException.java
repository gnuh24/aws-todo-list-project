package aws.todolist.api_gateway.exceptions.JwtException;

public class MalformedTokenException extends RuntimeException {
    public MalformedTokenException(String message) {
        super(message);
    }
}
