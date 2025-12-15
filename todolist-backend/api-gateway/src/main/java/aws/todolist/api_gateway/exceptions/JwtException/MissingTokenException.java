package aws.todolist.api_gateway.exceptions.JwtException;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class MissingTokenException extends GenericJwtException {
	
	public MissingTokenException() {
		super(
			SystemErrorCode.AUTH_MISSING_TOKEN,
			"Thiếu token xác thực"
		);
	}
	
	public MissingTokenException(String message) {
		super(SystemErrorCode.AUTH_MISSING_TOKEN, message);
	}
}