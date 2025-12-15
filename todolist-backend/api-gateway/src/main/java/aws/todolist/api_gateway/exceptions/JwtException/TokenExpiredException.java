package aws.todolist.api_gateway.exceptions.JwtException;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class TokenExpiredException extends GenericJwtException {
	
	public TokenExpiredException() {
		super(
			SystemErrorCode.AUTH_EXPIRED_TOKEN,
			"Token đã hết hạn"
		);
	}
	
	public TokenExpiredException(String message) {
		super(SystemErrorCode.AUTH_EXPIRED_TOKEN, message);
	}
}
