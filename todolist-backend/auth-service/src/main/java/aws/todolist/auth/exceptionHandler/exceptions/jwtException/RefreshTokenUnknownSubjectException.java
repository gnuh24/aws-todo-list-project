package aws.todolist.auth.exceptionHandler.exceptions.jwtException;


import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenUnknownSubjectException extends GenericJwtException {
	
	public RefreshTokenUnknownSubjectException() {
		super(
			SystemErrorCode.AUTH_REFRESH_TOKEN_UNKNOWN_SUBJECT,
			"Refresh token chứa subject không tồn tại"
		);
	}
	
	public RefreshTokenUnknownSubjectException(String message) {
		super(
			SystemErrorCode.AUTH_REFRESH_TOKEN_UNKNOWN_SUBJECT,
			message
		);
	}
}
