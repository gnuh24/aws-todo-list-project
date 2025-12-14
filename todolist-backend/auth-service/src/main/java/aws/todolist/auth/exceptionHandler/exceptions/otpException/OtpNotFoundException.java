package aws.todolist.auth.exceptionHandler.exceptions.otpException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class OtpNotFoundException extends OtpException {

    public OtpNotFoundException() {
        super(
            SystemErrorCode.AUTH_OTP_NOT_FOUND,
            "Không tìm thấy mã OTP"
        );
    }
}
