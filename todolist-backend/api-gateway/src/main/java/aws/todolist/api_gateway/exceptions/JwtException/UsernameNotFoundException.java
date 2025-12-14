package aws.todolist.api_gateway.exceptions.JwtException;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class UsernameNotFoundException extends GenericJwtException {
	
	public UsernameNotFoundException() {
		super(
			SystemErrorCode.AUTH_TOKEN_UNKNOWN_SUBJECT,
			"Subject trong token không tồn tại"
		);
	}
	
	public UsernameNotFoundException(String message) {
		super(SystemErrorCode.AUTH_TOKEN_UNKNOWN_SUBJECT, message);
	}
}
