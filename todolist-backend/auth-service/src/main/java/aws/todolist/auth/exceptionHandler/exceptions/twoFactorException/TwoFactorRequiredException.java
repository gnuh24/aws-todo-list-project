package aws.todolist.auth.exceptionHandler.exceptions.twoFactorException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class TwoFactorRequiredException extends TwoFactorException {

    public TwoFactorRequiredException() {
        super(
            SystemErrorCode.AUTH_2FA_REQUIRED,
            "Cần xác thực bổ sung để tiếp tục"
        );
    }
}
