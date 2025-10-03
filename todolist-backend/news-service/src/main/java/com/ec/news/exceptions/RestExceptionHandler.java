package com.ec.news.exceptions;

import com.ec.news.aop.AppLogger;
import com.ec.news.exceptions.AuthException.StepUpAuthenticationException;
import com.ec.news.exceptions.JwtException.*;
import com.ec.news.exceptions.business.NewsContentInvalidHtmlException;
import com.ec.news.exceptions.business.NewsNotFoundException;
import com.ec.news.exceptions.business.NewsTitleAlreadyExistsException;
import com.ec.news.exceptions.errorCode.NewsBusinessErrorCode;
import com.ec.news.exceptions.errorCode.SystemErrorCode;
import com.ec.news.exceptions.fileException.EmptyFileUploadException;
import com.ec.news.exceptions.fileException.InvalidFileTypeException;
import com.ec.news.exceptions.otpException.OtpNotFoundException;
import com.ec.news.utils.EnvironmentUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.util.*;

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
		
		appLogger.error(request, "❌ [{}] {} - {}", code, message, ex.getMessage());
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
		appLogger.warn(servletRequest, "🟠 Validation failed: {}", ex.getMessage());
		
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
	
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<Object> handleFileNotFound(HttpServletRequest request, FileNotFoundException ex) {
		return buildErrorResponse(request, HttpStatus.NOT_FOUND, SystemErrorCode.SYS_FILE_NOT_FOUND, "Không tìm thấy tệp", ex, null);
	}
	
	@ExceptionHandler(NewsNotFoundException.class)
	public ResponseEntity<Object> handleNewsNotFound(
	    HttpServletRequest request,
	    NewsNotFoundException ex
	) {
		return buildErrorResponse(
		    request,
		    HttpStatus.NOT_FOUND,
		    NewsBusinessErrorCode.NEWS_NOT_FOUND, // mã lỗi bạn đã định nghĩa: "NEWS-001"
		    ex.getMessage(),
		    ex,
		    null
		);
	}
	
	
	@ExceptionHandler(EmptyFileUploadException.class)
	public ResponseEntity<Object> handleEmptyFileUpload(HttpServletRequest request, EmptyFileUploadException ex) {
		return buildErrorResponse(
		    request,
		    HttpStatus.BAD_REQUEST,
		    SystemErrorCode.SYS_FILE_UPLOAD_FAILED,
		    "File upload không được để trống",
		    ex,
		    null
		);
	}
	
	@ExceptionHandler(InvalidFileTypeException.class)
	public ResponseEntity<Object> handleInvalidFileType(HttpServletRequest request, InvalidFileTypeException ex) {
		return buildErrorResponse(
		    request,
		    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
		    SystemErrorCode.SYS_FILE_UNSUPPORTED_TYPE ,
		    "Chỉ cho phép upload các định dạng ảnh như jpg, png...",
		    ex,
		    null
		);
	}
	
	@ExceptionHandler(NewsContentInvalidHtmlException.class)
	public ResponseEntity<Object> handleInvalidContent(
	    HttpServletRequest request, NewsContentInvalidHtmlException ex) {
		return buildErrorResponse(
		    request,
		    HttpStatus.BAD_REQUEST,
		    NewsBusinessErrorCode.NEWS_CONTENT_INVALID_HTML,
		    ex.getMessage(),
		    ex,
		    null
		);
	}
	
	
	
	@ExceptionHandler(InvalidTokenTypeException.class)
	public ResponseEntity<Object> handleReTypeException(HttpServletRequest request, InvalidTokenTypeException ex) {
		String code = SystemErrorCode.AUTH_INVALID_CREDENTIALS;
		String message = "Xác thực thất bại";
		
		
		code = SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_TYP;
		message = "Token chứa type không hợp lệ.";
		
		
		return buildErrorResponse(request, HttpStatus.UNAUTHORIZED, code, message, ex, null);
	}
	
	@ExceptionHandler(NewsTitleAlreadyExistsException.class)
	public ResponseEntity<Object> handleTitleExists(
	    HttpServletRequest request, NewsTitleAlreadyExistsException ex) {
		return buildErrorResponse(
		    request,
		    HttpStatus.CONFLICT,
		    NewsBusinessErrorCode.NEWS_TITLE_ALREADY_EXISTS,
		    ex.getMessage(),
		    ex,
		    null
		);
	}
	
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(HttpServletRequest request, AuthenticationException ex) {
		String code = SystemErrorCode.AUTH_INVALID_CREDENTIALS;
		String message = "Xác thực thất bại";
		
		// ==== NHÓM: LOGIN / TÀI KHOẢN ====
		if (ex instanceof BadCredentialsException) {
			code = SystemErrorCode.AUTH_INVALID_CREDENTIALS;
			message = "Email hoặc mật khẩu không đúng!";
		} else if (ex instanceof LockedException) {
			code = SystemErrorCode.AUTH_ACCOUNT_LOCKED;
			message = "Tài khoản đã bị khóa!";
		} else if (ex instanceof DisabledException) {
			code = SystemErrorCode.AUTH_ACCOUNT_INACTIVE;
			message = "Tài khoản chưa được kích hoạt!";
		} else if (ex instanceof UsernameNotFoundException) {
			code = SystemErrorCode.AUTH_ACCOUNT_NOT_FOUND;
			message = "Tài khoản không tồn tại!";
		}
		
		// ==== NHÓM: REFRESH TOKEN ====
		else if (ex instanceof RefreshTokenNotFound) {
			code = SystemErrorCode.AUTH_MISSING_REFRESH_TOKEN;
			message = "Không tìm thấy refresh token.";
		} else if (ex instanceof RefreshTokenExpiredException) {
			code = SystemErrorCode.AUTH_REFRESH_TOKEN_EXPIRED;
			message = "Refresh token đã hết hạn.";
		} else if (ex instanceof RefreshTokenBlacklistedException) {
			code = SystemErrorCode.AUTH_REFRESH_TOKEN_BLACKLISTED;
			message = "Refresh token đã bị thu hồi hoặc không hợp lệ.";
		}
		
		// ==== NHÓM: TOKEN KHÁC ====
		else if (ex instanceof InvalidTokenTypeException) {
			code = SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_TYP;
			message = "Token chứa type không hợp lệ.";
		} else if (ex instanceof InvalidJWTSignatureException) {
			code = SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_SIGNATURE;
			message = "Token có chữ ký không hợp lệ.";
		}
		
		return buildErrorResponse(request, HttpStatus.UNAUTHORIZED, code, message, ex, null);
	}
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneric(HttpServletRequest request, Exception ex) {
		if (ex instanceof MaxUploadSizeExceededException) {
			return buildErrorResponse(
			    request,
			    HttpStatus.PAYLOAD_TOO_LARGE,
			    SystemErrorCode.SYS_FILE_TOO_LARGE,
			    "File quá lớn",
			    ex,
			    null
			);
		}
		
		return buildErrorResponse(
		    request,
		    HttpStatus.INTERNAL_SERVER_ERROR,
		    SystemErrorCode.SYSTEM_UNKNOWN_ERROR,
		    "Lỗi không xác định",
		    ex,
		    null
		);
	}

	
	
	@ExceptionHandler(OtpNotFoundException.class)
	public ResponseEntity<Object> handleOtpNotFound(HttpServletRequest request, OtpNotFoundException ex) {
		return buildErrorResponse(request, HttpStatus.BAD_REQUEST,
		    SystemErrorCode.AUTH_OTP_NOT_FOUND,
		    ex.getMessage(),
		    ex,
		    null);
	}
	
	
	@ExceptionHandler(StepUpAuthenticationException.class)
	public ResponseEntity<Object> handleStepUpAuthFail(HttpServletRequest request, StepUpAuthenticationException ex) {
		return buildErrorResponse(request, HttpStatus.UNAUTHORIZED,
		    SystemErrorCode.AUTH_2FA_FAILED,
		    "Xác thực bổ sung không thành công. Vui lòng kiểm tra lại mật khẩu.",
		    ex,
		    null);
	}
	
	
}
