package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class InvalidTokenTypeException extends GenericJwtException {
	public InvalidTokenTypeException() {
		super(
			SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_TYP,
			"Token chứa type không hợp lệ."
		);
	}
}

