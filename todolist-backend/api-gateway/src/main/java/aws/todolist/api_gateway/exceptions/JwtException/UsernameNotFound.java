package aws.todolist.api_gateway.exceptions.JwtException;

public class UsernameNotFound extends GenericJwtException {
    public UsernameNotFound(String message) {
        super(message);
    }
}
