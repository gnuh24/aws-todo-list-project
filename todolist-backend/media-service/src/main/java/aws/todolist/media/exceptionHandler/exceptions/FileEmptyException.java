package aws.todolist.media.exceptionHandler.exceptions;

import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;

public class FileEmptyException extends GenericMediaException {

    public FileEmptyException() {
        super(
            SystemErrorCode.SYS_FILE_EMPTY,
            "File rỗng hoặc không tồn tại."
        );
    }
}