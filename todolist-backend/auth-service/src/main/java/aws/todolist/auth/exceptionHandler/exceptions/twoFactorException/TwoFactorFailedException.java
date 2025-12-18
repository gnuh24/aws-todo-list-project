package aws.todolist.auth.exceptionHandler.exceptions.twoFactorException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;
import lombok.Getter;

@Getter
public class TwoFactorFailedException extends TwoFactorException {
	
	public TwoFactorFailedException() {
		super(
			SystemErrorCode.AUTH_2FA_FAILED,
			"Xác thực hai lớp thất bại"
		);
	}
	
	public TwoFactorFailedException(String message) {
		super(
			SystemErrorCode.AUTH_2FA_FAILED,
			message
		);
	}
}
