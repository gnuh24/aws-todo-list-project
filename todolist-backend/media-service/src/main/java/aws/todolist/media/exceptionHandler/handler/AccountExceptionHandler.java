package aws.todolist.media.exceptionHandler.handler;

import aws.todolist.media.exceptionHandler.ErrorResponse;
import aws.todolist.media.exceptionHandler.errorCode.BusinessErrorCode;
import aws.todolist.media.exceptionHandler.errorCode.SystemErrorCode;
import aws.todolist.media.exceptionHandler.exceptions.GenericMediaException;
import aws.todolist.media.utils.EnvironmentUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(10)
public class AccountExceptionHandler {
	
	@Autowired
	private  EnvironmentUtils environmentUtils;

	
	/* =====================================================
	 *                     BUILDER
	 * ===================================================== */
	
	private ResponseEntity<Object> build(
		HttpServletRequest request,
		HttpStatus status,
		String code,
		String message,
		Exception ex
	) {
		ErrorResponse response =
			new ErrorResponse(status.value(), code, message, null, null);
		
		if (environmentUtils.isDevMode()) {
			response.setDetailMessage(ex.toString());
		}
		
		return ResponseEntity
			.status(status)
			.contentType(MediaType.APPLICATION_JSON)
			.body(response);
	}
	
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<Object> handleMissingRequestHeaderException(
		HttpServletRequest request,
		MissingRequestHeaderException ex
	) {
		String message = String.format(
			"Thiếu header bắt buộc: %s",
			ex.getHeaderName()
		);
		
		return build(
			request,
			HttpStatus.BAD_REQUEST,
			SystemErrorCode.SYS_MISSING_REQUIRED_FIELD,
			message,
			ex
		);
	}
	
	/**
	 * Handle all Media / File exceptions
	 */
	@ExceptionHandler(GenericMediaException.class)
	public ResponseEntity<Object> handleGenericMediaException(
		HttpServletRequest request,
		GenericMediaException ex
	) {
		return build(
			request,
			resolveHttpStatus(ex.getErrorCode()),
			ex.getErrorCode(),
			ex.getMessage(),
			ex
		);
	}
	
	/**
	 * Resolve HTTP Status from error code
	 */
	private HttpStatus resolveHttpStatus(String errorCode) {
		
		return switch (errorCode) {
			case "SYS-FILE-001" -> HttpStatus.PAYLOAD_TOO_LARGE; // too large
			case "SYS-FILE-002" -> HttpStatus.BAD_REQUEST;      // unsupported type
			case "SYS-FILE-004" -> HttpStatus.NOT_FOUND;        // not found
			case "SYS-FILE-005" -> HttpStatus.BAD_REQUEST;      // empty file
			default -> HttpStatus.INTERNAL_SERVER_ERROR;        // upload failed
		};
	}
	
}
