package aws.todolist.api_gateway.exceptions.JwtException;

public class UnsupportedTokenException extends RuntimeException {
    public UnsupportedTokenException(String message) {
        super(message);
    }
}
