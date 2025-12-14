package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenBlacklistedException extends GenericJwtException {
	public RefreshTokenBlacklistedException() {
		super(
			SystemErrorCode.AUTH_REFRESH_TOKEN_BLACKLISTED,
			"Refresh token đã bị thu hồi hoặc không hợp lệ."
		);
	}
}
