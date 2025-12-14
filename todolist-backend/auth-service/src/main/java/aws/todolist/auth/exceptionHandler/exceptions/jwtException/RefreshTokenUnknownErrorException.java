package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenUnknownErrorException extends GenericJwtException {

    public RefreshTokenUnknownErrorException() {
        super(
            SystemErrorCode.AUTH_REFRESH_TOKEN_UNKNOWN_ERROR,
            "Lỗi không xác định khi xử lý refresh token."
        );
    }
}