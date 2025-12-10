package aws.todolist.api_gateway.exceptions.JwtException;


public class AccessTokenBlacklistedException extends GenericJwtException {
    public AccessTokenBlacklistedException(String message) {
        super(message);
    }
}