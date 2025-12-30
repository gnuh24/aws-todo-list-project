package aws.todolist.media.exceptionHandler.exceptions;

import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;

public class FileNotFoundException extends GenericMediaException {

    public FileNotFoundException() {
        super(
            SystemErrorCode.SYS_FILE_NOT_FOUND,
            "Không tìm thấy file yêu cầu."
        );
    }
}