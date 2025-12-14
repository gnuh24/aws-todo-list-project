package aws.todolist.auth.exceptionHandler.exceptions.loginException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class InvalidCredentialsException extends LoginException {
    public InvalidCredentialsException() {
        super(
            SystemErrorCode.AUTH_INVALID_CREDENTIALS,
            "Email hoặc mật khẩu không đúng"
        );
    }
}