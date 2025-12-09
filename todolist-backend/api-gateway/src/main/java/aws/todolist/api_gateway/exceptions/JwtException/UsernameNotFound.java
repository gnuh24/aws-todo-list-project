package aws.todolist.api_gateway.exceptions.JwtException;

public class UsernameNotFound extends AuthenticationException {
    public UsernameNotFound(String message) {
        super(message);
    }
}
