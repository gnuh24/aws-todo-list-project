package aws.todolist.api_gateway.exceptions.JwtException;


import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class InvalidJWTSignatureException extends GenericJwtException {
	public InvalidJWTSignatureException() {
		super(
			SystemErrorCode.AUTH_TOKEN_INVALID_SIGNATURE,
			"Token có chữ ký không hợp lệ."
		);
	}
}

