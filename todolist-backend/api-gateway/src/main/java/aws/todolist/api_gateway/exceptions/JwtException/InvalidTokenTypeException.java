package aws.todolist.api_gateway.exceptions.JwtException;


import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class InvalidTokenTypeException extends GenericJwtException {
	public InvalidTokenTypeException() {
		super(
			SystemErrorCode.AUTH_TOKEN_INVALID_TYP,
			"Token chứa type không hợp lệ."
		);
	}
}

