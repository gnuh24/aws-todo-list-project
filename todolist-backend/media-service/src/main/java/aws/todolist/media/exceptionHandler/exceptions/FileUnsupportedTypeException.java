package aws.todolist.media.exceptionHandler.exceptions;

import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;

public class FileUnsupportedTypeException extends GenericMediaException {

    public FileUnsupportedTypeException() {
        super(
            SystemErrorCode.SYS_FILE_UNSUPPORTED_TYPE,
            "Định dạng file không được hỗ trợ."
        );
    }
}