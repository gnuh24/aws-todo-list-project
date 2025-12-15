package aws.todolist.auth.exceptionHandler.exceptions.jwtException;


import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class InvalidJWTSignatureException extends GenericJwtException {
	public InvalidJWTSignatureException() {
		super(
			SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_SIGNATURE,
			"Token có chữ ký không hợp lệ."
		);
	}
}
