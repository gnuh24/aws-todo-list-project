package aws.todolist.media.exceptionHandler.exceptions;

import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;

public class FileUploadFailedException extends GenericMediaException {

    public FileUploadFailedException() {
        super(
            SystemErrorCode.SYS_FILE_UPLOAD_FAILED,
            "Lỗi xảy ra trong quá trình upload file."
        );
    }
}
