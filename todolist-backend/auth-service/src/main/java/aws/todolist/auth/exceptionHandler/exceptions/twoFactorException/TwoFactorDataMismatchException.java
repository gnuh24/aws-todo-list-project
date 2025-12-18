package aws.todolist.auth.exceptionHandler.exceptions.twoFactorException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class TwoFactorDataMismatchException extends TwoFactorException {

    public TwoFactorDataMismatchException() {
        super(
            SystemErrorCode.AUTH_HMAC_MISMATCH,
            "Dữ liệu xác thực không hợp lệ hoặc đã bị thay đổi"
        );
    }
}
