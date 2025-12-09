package aws.todolist.api_gateway.exceptions.JwtException;


public class AccessTokenBlacklistedException extends AuthenticationException {
    public AccessTokenBlacklistedException(String message) {
        super(message);
    }
}