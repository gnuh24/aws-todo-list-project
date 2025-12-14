package aws.todolist.user.exceptionHandler.handler;

import aws.todolist.user.exceptionHandler.ErrorResponse;
import aws.todolist.user.exceptionHandler.errorCode.BusinessErrorCode;
import aws.todolist.user.exceptionHandler.errorCode.SystemErrorCode;
import aws.todolist.user.exceptionHandler.exceptions.AccountNotFoundException;
import aws.todolist.user.utils.EnvironmentUtils;
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
	
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Object> handleAccountNotFoundException(HttpServletRequest request, AccountNotFoundException ex) {
		return build(
			request,
			HttpStatus.NOT_FOUND,
			BusinessErrorCode.ACCOUNT_PROFILE_NOT_FOUND,
			ex.getMessage(),
			ex
		);	}
	
}
