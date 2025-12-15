package aws.todolist.api_gateway.exceptions.JwtException;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class MalformedTokenException extends GenericJwtException {
	
	public MalformedTokenException() {
		super(
			SystemErrorCode.AUTH_TOKEN_MALFORMED,
			"Token không đúng định dạng"
		);
	}
	
	public MalformedTokenException(String message) {
		super(SystemErrorCode.AUTH_TOKEN_MALFORMED, message);
	}
}