package aws.todolist.auth.exceptionHandler.exceptions.loginException;

import lombok.Getter;

@Getter
public abstract class LoginException extends RuntimeException {

    private final String code;

    protected LoginException(String code, String message) {
        super(message);
        this.code = code;
    }
	
}
