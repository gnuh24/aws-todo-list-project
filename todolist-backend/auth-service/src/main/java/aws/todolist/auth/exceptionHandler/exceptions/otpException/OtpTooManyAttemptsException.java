package aws.todolist.auth.exceptionHandler.exceptions.otpException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class OtpTooManyAttemptsException extends OtpException {

    public OtpTooManyAttemptsException() {
        super(
            SystemErrorCode.AUTH_OTP_TOO_MANY_ATTEMPTS,
            "Bạn đã nhập sai OTP quá nhiều lần"
        );
    }
}
