package aws.todolist.auth.exceptionHandler.exceptions.jwtException;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;

public class RefreshTokenMalformedException extends GenericJwtException {

    public RefreshTokenMalformedException() {
        super(
            SystemErrorCode.AUTH_REFRESH_TOKEN_MALFORMED,
            "Refresh token không đúng định dạng."
        );
    }
}