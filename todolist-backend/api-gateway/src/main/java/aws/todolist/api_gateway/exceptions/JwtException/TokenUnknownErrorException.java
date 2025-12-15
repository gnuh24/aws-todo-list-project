package aws.todolist.api_gateway.exceptions.JwtException;


import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;

public class TokenUnknownErrorException extends GenericJwtException {

    public TokenUnknownErrorException() {
        super(
            SystemErrorCode.AUTH_TOKEN_UNKNOWN_ERROR,
            "Lỗi không xác định khi xử lý token."
        );
    }
}