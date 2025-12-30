package aws.todolist.media.exceptionHandler.exceptions;

import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;

import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;

public class FileTooLargeException extends GenericMediaException {
	
	public FileTooLargeException() {
		super(
			SystemErrorCode.SYS_FILE_TOO_LARGE,
			"File vượt quá dung lượng cho phép."
		);
	}
}