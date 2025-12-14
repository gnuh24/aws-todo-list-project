package aws.todolist.auth.exceptionHandler.exceptions;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;
import lombok.Getter;

@Getter
public class TwoFactorFailedException extends RuntimeException {

    private final String code = SystemErrorCode.AUTH_2FA_FAILED;

    public TwoFactorFailedException(String message) {
        super(message);
    }

}
