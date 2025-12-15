package aws.todolist.auth.exceptionHandler.exceptions.otpException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class OtpExpiredException extends OtpException {

    public OtpExpiredException() {
        super(
            SystemErrorCode.AUTH_OTP_EXPIRED,
            "Mã OTP đã hết hạn"
        );
    }
}
