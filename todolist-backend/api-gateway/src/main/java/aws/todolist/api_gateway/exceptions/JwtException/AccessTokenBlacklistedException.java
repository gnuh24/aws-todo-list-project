package aws.todolist.api_gateway.exceptions.JwtException;


import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class AccessTokenBlacklistedException extends GenericJwtException {
	
	public AccessTokenBlacklistedException() {
		super(
			SystemErrorCode.AUTH_TOKEN_BLACKLISTED,
			"Access token đã bị thu hồi"
		);
	}
	
	public AccessTokenBlacklistedException(String message) {
		super(SystemErrorCode.AUTH_TOKEN_BLACKLISTED, message);
	}
}