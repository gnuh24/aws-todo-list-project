package aws.todolist.taskflow.exceptions.handler;

import aws.todolist.taskflow.exceptions.DetailError;
import aws.todolist.taskflow.exceptions.ErrorResponse;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.ProjectException.ResourceNotFoundException;
import aws.todolist.taskflow.logging.AppLogger;
import aws.todolist.taskflow.utils.EnvironmentUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;


@RestControllerAdvice
@Order(10)
public class ProjectExceptionHandler extends ResponseEntityExceptionHandler {

    private final AppLogger appLogger;
    private final EnvironmentUtils environmentUtils;

    public ProjectExceptionHandler(AppLogger appLogger,
                                   EnvironmentUtils environmentUtils) {
        this.appLogger = appLogger;
        this.environmentUtils = environmentUtils;
    }


    private ResponseEntity<Object> build(
            HttpServletRequest request,
            HttpStatus status,
            String code,
            String message,
            Exception ex,
            List<DetailError> details
    ) {
        ErrorResponse response =
                new ErrorResponse(status.value(), code, message, null, details);

        if (environmentUtils.isDevMode()) {
            response.setDetailMessage(ex.toString());
        }


        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    // 🔒 403 - Không có quyền
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(
            HttpServletRequest request,
            ForbiddenException ex) {

        return build(
                request,
                HttpStatus.FORBIDDEN,
                ex.getCode(),
                ex.getMessage(),
                ex,
                null
        );
    }

    // ❌ 400 - Request sai
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(
            HttpServletRequest request,
            BadRequestException ex) {

        return build(
                request,
                HttpStatus.BAD_REQUEST,
                ex.getCode(),
                ex.getMessage(),
                ex,
                null
        );
    }

    // 🔍 404 - Không tìm thấy resource
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(
            HttpServletRequest request,
            ResourceNotFoundException ex) {

        return build(
                request,
                HttpStatus.NOT_FOUND,
                ex.getCode(),
                ex.getMessage(),
                ex,
                null
        );
    }
}