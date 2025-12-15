package aws.todolist.auth.exceptionHandler.exceptions.otpException;

import lombok.Getter;

@Getter
public abstract class OtpException extends RuntimeException {
	
	private final String code;
	
	protected OtpException(String code, String message) {
		super(message);
		this.code = code;
	}
}
