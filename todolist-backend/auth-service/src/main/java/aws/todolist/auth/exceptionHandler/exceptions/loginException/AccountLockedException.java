package aws.todolist.auth.exceptionHandler.exceptions.loginException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class AccountLockedException extends LoginException {
    public AccountLockedException() {
        super(
            SystemErrorCode.AUTH_ACCOUNT_LOCKED,
            "Tài khoản đã bị khóa"
        );
    }
}