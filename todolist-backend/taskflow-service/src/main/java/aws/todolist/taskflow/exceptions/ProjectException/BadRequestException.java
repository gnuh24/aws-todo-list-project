package aws.todolist.taskflow.exceptions.ProjectException;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private final String code;

    public BadRequestException(String code, String message) {
        super(message);
        this.code = code;
    }

}