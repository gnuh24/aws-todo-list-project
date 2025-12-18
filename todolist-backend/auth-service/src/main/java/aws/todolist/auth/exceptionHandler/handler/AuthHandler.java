package aws.todolist.auth.exceptionHandler.handler;

import aws.todolist.auth.exceptionHandler.ErrorResponse;
import aws.todolist.auth.exceptionHandler.exceptions.DeleteConfirmationRequiredException;
import aws.todolist.auth.exceptionHandler.exceptions.jwtException.GenericJwtException;
import aws.todolist.auth.exceptionHandler.exceptions.loginException.LoginException;
import aws.todolist.auth.exceptionHandler.exceptions.otpException.OtpException;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorException;
import aws.todolist.auth.utils.EnvironmentUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(10)
public class AuthHandler {
	
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
	
	/* =====================================================
	 *               AUTHENTICATION (LOGIN)
	 * ===================================================== */
	
	@ExceptionHandler(LoginException.class)
	public ResponseEntity<Object> handleLogin(
		HttpServletRequest request,
		LoginException ex) {
		
		return build(
			request,
			HttpStatus.UNAUTHORIZED,
			ex.getCode(),
			ex.getMessage(),
			ex
		);
	}
	
	
	/* =====================================================
	 *               JWT / REFRESH TOKEN
	 * ===================================================== */
	
	@ExceptionHandler(GenericJwtException.class)
	public ResponseEntity<Object> handleJwt(
		HttpServletRequest request,
		GenericJwtException ex) {
		
		return build(
			request,
			HttpStatus.UNAUTHORIZED,
			ex.getErrorCode(),
			ex.getMessage(),
			ex
		);
	}
	
	@ExceptionHandler(OtpException.class)
	public ResponseEntity<Object> handleOtpException(
		HttpServletRequest request,
		OtpException ex) {
		
		return build(
			request,
			HttpStatus.BAD_REQUEST,
			ex.getCode(),
			ex.getMessage(),
			ex
		);
	}
	
	/* =====================================================
	 *               TWO FACTOR AUTHENTICATION
	 * ===================================================== */
	
	@ExceptionHandler(TwoFactorException.class)
	public ResponseEntity<Object> handleTwoFactorException(
		HttpServletRequest request,
		TwoFactorException ex) {
		
		return build(
			request,
			HttpStatus.BAD_REQUEST,
			ex.getCode(),
			ex.getMessage(),
			ex
		);
	}
	
	
	/* =====================================================
	 *           DELETE ACCOUNT – CONFIRMATION
	 * ===================================================== */
	
	@ExceptionHandler(DeleteConfirmationRequiredException.class)
	public ResponseEntity<Object> handleDeleteConfirmationRequired(
		HttpServletRequest request,
		DeleteConfirmationRequiredException ex) {
		
		return build(
			request,
			HttpStatus.BAD_REQUEST,
			ex.getCode(),
			ex.getMessage(),
			ex
		);
	}
	
	
	
	
}
