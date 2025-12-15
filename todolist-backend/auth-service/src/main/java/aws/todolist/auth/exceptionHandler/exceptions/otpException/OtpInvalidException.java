package aws.todolist.auth.exceptionHandler.exceptions.otpException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class OtpInvalidException extends OtpException {

    public OtpInvalidException() {
        super(
            SystemErrorCode.AUTH_OTP_INVALID,
            "Mã OTP không hợp lệ"
        );
    }
}
