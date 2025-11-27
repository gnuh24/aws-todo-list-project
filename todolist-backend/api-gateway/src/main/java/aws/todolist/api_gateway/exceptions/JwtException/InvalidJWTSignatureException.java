package aws.todolist.api_gateway.exceptions.JwtException;


public class InvalidJWTSignatureException extends AuthenticationException {
    public InvalidJWTSignatureException(String message) {
        super(message);
    }
}
