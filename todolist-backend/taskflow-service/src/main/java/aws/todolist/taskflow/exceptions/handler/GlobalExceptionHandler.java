package aws.todolist.taskflow.exceptions.handler;

import aws.todolist.taskflow.exceptions.DetailError;
import aws.todolist.taskflow.exceptions.ErrorResponse;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.logging.AppLogger;
import aws.todolist.taskflow.utils.EnvironmentUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Order(100)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final AppLogger appLogger;
    private final EnvironmentUtils environmentUtils;

    public GlobalExceptionHandler(AppLogger appLogger,
                                  EnvironmentUtils environmentUtils) {
        this.appLogger = appLogger;
        this.environmentUtils = environmentUtils;
    }

    /* =====================================================
     *                     BUILDER
     * ===================================================== */

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

    private HttpServletRequest req(WebRequest request) {
        return (HttpServletRequest) request.resolveReference(WebRequest.REFERENCE_REQUEST);
    }

    /* =====================================================
     *                     HTTP ERRORS
     * ===================================================== */

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            @NonNull NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        return build(req(request),
                HttpStatus.NOT_FOUND,
                SystemErrorCode.API_NOT_FOUND,
                "API không tồn tại",
                ex,
                null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NonNull HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        return build(req(request),
                HttpStatus.METHOD_NOT_ALLOWED,
                SystemErrorCode.API_METHOD_NOT_ALLOWED,
                "Phương thức không được hỗ trợ",
                ex,
                null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            @NonNull HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        return build(req(request),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                SystemErrorCode.API_UNSUPPORTED_MEDIA_TYPE,
                "Không hỗ trợ định dạng gửi lên",
                ex,
                null);
    }

    /* =====================================================
     *                     VALIDATION
     * ===================================================== */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        List<DetailError> details = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.add(new DetailError(
                    SystemErrorCode.SYS_VALIDATION_ERROR,
                    fe.getField() + ": " + fe.getDefaultMessage()
            ));
        }

        return build(req(request),
                HttpStatus.BAD_REQUEST,
                SystemErrorCode.SYS_VALIDATION_ERROR,
                "Dữ liệu đầu vào không hợp lệ",
                ex,
                details);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        return build(req(request),
                HttpStatus.BAD_REQUEST,
                SystemErrorCode.SYS_MISSING_REQUIRED_FIELD,
                "Thiếu tham số bắt buộc",
                ex,
                null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            HttpServletRequest request,
            ConstraintViolationException ex) {

        List<DetailError> details = new ArrayList<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            details.add(new DetailError(
                    SystemErrorCode.SYS_CONSTRAINT_VIOLATION,
                    v.getPropertyPath() + ": " + v.getMessage()
            ));
        }

        return build(request,
                HttpStatus.BAD_REQUEST,
                SystemErrorCode.SYS_CONSTRAINT_VIOLATION,
                "Vi phạm ràng buộc dữ liệu",
                ex,
                details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(
            HttpServletRequest request,
            MethodArgumentTypeMismatchException ex) {

        return build(request,
                HttpStatus.BAD_REQUEST,
                SystemErrorCode.SYS_INVALID_FORMAT,
                "Kiểu dữ liệu không hợp lệ",
                ex,
                null);
    }


    /* =====================================================
     *                     NOT FOUND
     * ===================================================== */

    @ExceptionHandler({
            EntityNotFoundException.class,
    })
    public ResponseEntity<Object> handleNotFound(
            HttpServletRequest request,
            Exception ex) {

        return build(request,
                HttpStatus.NOT_FOUND,
                SystemErrorCode.SYS_RESOURCE_NOT_FOUND,
                ex.getMessage(),
                ex,
                null);
    }


    /* =====================================================
     *                     FALLBACK
     * ===================================================== */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknown(
            HttpServletRequest request,
            Exception ex) {

        return build(request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                SystemErrorCode.SYS_INTERNAL_SERVER_ERROR,
                "Lỗi hệ thống",
                ex,
                null);
    }
}
