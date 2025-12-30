package aws.todolist.media.exceptionHandler.exceptions;

import lombok.Getter;

public abstract class GenericMediaException extends RuntimeException {

    @Getter
    private final String errorCode;

    protected GenericMediaException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}