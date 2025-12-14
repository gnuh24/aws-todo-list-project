package aws.todolist.auth.exceptionHandler.exceptions.loginException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class AccountInactiveException extends LoginException {
    public AccountInactiveException() {
        super(
            SystemErrorCode.AUTH_ACCOUNT_INACTIVE,
            "Tài khoản chưa được kích hoạt"
        );
    }
}