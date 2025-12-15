package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenExpiredException extends GenericJwtException {
	public RefreshTokenExpiredException() {
		super(
			SystemErrorCode.AUTH_REFRESH_TOKEN_EXPIRED,
			"Refresh token đã hết hạn."
		);
	}
}
