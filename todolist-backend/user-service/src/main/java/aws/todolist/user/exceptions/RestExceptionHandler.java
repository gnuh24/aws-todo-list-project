package aws.todolist.user.exceptions;

import aws.todolist.user.logging.AppLogger;
import aws.todolist.user.exceptions.errorCode.SystemErrorCode;
import aws.todolist.user.utils.EnvironmentUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private AppLogger appLogger;
	
	@Autowired
	private EnvironmentUtils environmentUtils;
	
	private ResponseEntity<Object> buildErrorResponse(HttpServletRequest request, HttpStatus status, String code, String message, Exception ex, List<DetailError> errors) {
		ErrorResponse response = new ErrorResponse(status.value(), code, message, null, errors);
		if (environmentUtils.isDevMode()) {
			response.setDetailMessage(ex.toString());
		}
		System.err.println(ex.getClass());
		return new ResponseEntity<>(response, status);
	}
	
	private HttpServletRequest getRequest(WebRequest webRequest) {
		return (HttpServletRequest) webRequest.resolveReference(WebRequest.REFERENCE_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		return buildErrorResponse(getRequest(request), HttpStatus.NOT_FOUND, SystemErrorCode.API_NOT_FOUND, "API không tồn tại", ex, null);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		return buildErrorResponse(getRequest(request), HttpStatus.METHOD_NOT_ALLOWED, SystemErrorCode.API_METHOD_NOT_ALLOWED, "Phương thức không được hỗ trợ", ex, null);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		return buildErrorResponse(getRequest(request), HttpStatus.UNSUPPORTED_MEDIA_TYPE, SystemErrorCode.API_UNSUPPORTED_MEDIA_TYPE, "Không hỗ trợ định dạng gửi lên", ex, null);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
	    MethodArgumentNotValidException ex,
	    @NonNull HttpHeaders headers,
	    @NonNull HttpStatusCode status,
	    @NonNull WebRequest request
	) {
		List<DetailError> details = new ArrayList<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			String detail = fieldError.getField() + ": " + fieldError.getDefaultMessage();
			details.add(new DetailError(SystemErrorCode.SYS_VALIDATION_ERROR, detail));
		}
		
		HttpServletRequest servletRequest = getRequest(request);
//		appLogger.warn(servletRequest, "🟠 Validation failed: {}", ex.getMessage());
		
		return buildErrorResponse(
		    servletRequest,
		    HttpStatus.BAD_REQUEST,
		    SystemErrorCode.SYS_VALIDATION_ERROR,
		    "Dữ liệu đầu vào không hợp lệ",
		    ex,
		    details
		);
	}
	
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		return buildErrorResponse(getRequest(request), HttpStatus.BAD_REQUEST, SystemErrorCode.SYS_MISSING_REQUIRED_FIELD, "Thiếu tham số bắt buộc", ex, null);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(HttpServletRequest request, ConstraintViolationException ex) {
		List<DetailError> details = new ArrayList<>();
		for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
			details.add(new DetailError(SystemErrorCode.SYS_CONSTRAINT_VIOLATION, v.getPropertyPath() + ": " + v.getMessage()));
		}
		return buildErrorResponse(request, HttpStatus.BAD_REQUEST, SystemErrorCode.SYS_CONSTRAINT_VIOLATION, "Vi phạm ràng buộc dữ liệu", ex, details);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleTypeMismatch(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
		return buildErrorResponse(request, HttpStatus.BAD_REQUEST, SystemErrorCode.SYS_INVALID_FORMAT, "Kiểu dữ liệu không hợp lệ", ex, null);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFound(HttpServletRequest request, EntityNotFoundException ex) {
		return buildErrorResponse(request, HttpStatus.NOT_FOUND, SystemErrorCode.SYS_FILE_NOT_FOUND, ex.getMessage(), ex, null);
	}
	
	@ExceptionHandler(aws.todolist.user.exceptions.AccountNotFoundException.class)
	public ResponseEntity<Object> handleAccountNotFoundException(HttpServletRequest request, aws.todolist.user.exceptions.AccountNotFoundException ex) {
		return buildErrorResponse(request, HttpStatus.NOT_FOUND, SystemErrorCode.ACCOUNT_PROFILE_NOT_FOUND, ex.getMessage(), ex, null);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneric(HttpServletRequest request, Exception ex) {
		return buildErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, SystemErrorCode.SYSTEM_UNKNOWN_ERROR, "Lỗi không xác định", ex, null);
	}
	
	
	
}
