package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenNotFoundException extends GenericJwtException {
	public RefreshTokenNotFoundException() {
		super(
			SystemErrorCode.AUTH_MISSING_REFRESH_TOKEN,
			"Không tìm thấy refresh token."
		);
	}
}
