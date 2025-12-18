package aws.todolist.auth.exceptionHandler.exceptions.twoFactorException;

import lombok.Getter;

@Getter
public abstract class TwoFactorException extends RuntimeException {

    private final String code;

    protected TwoFactorException(String code, String message) {
        super(message);
        this.code = code;
    }
}
