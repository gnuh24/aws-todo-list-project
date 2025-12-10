package aws.todolist.api_gateway.exceptions.JwtException;


public class InvalidJWTSignatureException extends GenericJwtException {
    public InvalidJWTSignatureException(String message) {
        super(message);
    }
}
