package aws.todolist.api_gateway.exceptions.JwtException;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class UnsupportedTokenException extends GenericJwtException {
	
	public UnsupportedTokenException() {
		super(
			SystemErrorCode.AUTH_TOKEN_UNSUPPORTED,
			"Token sử dụng thuật toán không được hỗ trợ"
		);
	}
	
	public UnsupportedTokenException(String message) {
		super(SystemErrorCode.AUTH_TOKEN_UNSUPPORTED, message);
	}
}
