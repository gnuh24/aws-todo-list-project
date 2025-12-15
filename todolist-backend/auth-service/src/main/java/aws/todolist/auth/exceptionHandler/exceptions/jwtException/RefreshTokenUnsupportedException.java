package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenUnsupportedException extends GenericJwtException {

    public RefreshTokenUnsupportedException() {
        super(
            SystemErrorCode.AUTH_REFRESH_TOKEN_UNSUPPORTED,
            "Refresh token sử dụng thuật toán không được hỗ trợ."
        );
    }
}