package aws.todolist.auth.exceptionHandler.exceptions;

import aws.todolist.auth.exceptionHandler.errorCode.SystemErrorCode;
import lombok.Getter;

@Getter
public class DeleteConfirmationRequiredException extends RuntimeException {

    private final String code;

    public DeleteConfirmationRequiredException() {
        super("Vui lòng nhập 'delete' để xác nhận xóa tài khoản.");
        this.code = SystemErrorCode.AUTH_2FA_REQUIRED;
    }
}