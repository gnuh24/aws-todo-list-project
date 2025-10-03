package com.ec.catalog.exceptions.AuthException;

import com.ec.catalog.aop.AppLogger;
import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.exceptions.DetailError;
import com.ec.catalog.exceptions.ErrorResponse;
import com.ec.catalog.exceptions.JwtException.AccessTokenBlacklistedException;
import com.ec.catalog.exceptions.JwtException.AccessTokenExpiredException;
import com.ec.catalog.exceptions.JwtException.InvalidJWTSignatureException;
import com.ec.catalog.exceptions.JwtException.InvalidTokenTypeException;
import com.ec.catalog.exceptions.errorCode.SystemErrorCode;
import com.ec.catalog.utils.EnvironmentUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class AuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
	
	@Autowired
	private EnvironmentUtils environmentUtils;
	
	@Autowired
	private AppLogger appLogger;
	
	private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	private void writeJsonResponse(HttpServletResponse response, HttpStatus status, String code, String message, String detailMessage, List<DetailError> errors) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");
		
		if (environmentUtils.isDevMode()) {
			ErrorResponse devResponse = new ErrorResponse(status.value(), code, message, detailMessage, errors);
			response.getWriter().write(objectWriter.writeValueAsString(devResponse));
		} else {
			ApiResponse<Object> prodResponse = new ApiResponse<>(status.value(), message, null);
			response.getWriter().write(objectWriter.writeValueAsString(prodResponse));
		}
	}
	
	// 401 Unauthorized - Authentication failure (e.g., missing token, expired token, etc.)
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
		String message = "Bạn cần đăng nhập (token) để truy cập tài nguyên này.";
		String detailMessage = ex.toString();
		String errorCode = SystemErrorCode.AUTH_MISSING_TOKEN; // default fallback
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		
		appLogger.warn(request, "🛑 [{}] {} - {}", errorCode, message, ex.getMessage());
		
		// Có thể refine ở đây nếu có loại cụ thể (JWT expired, malformed, etc.)
		// Ví dụ nếu exception instanceof CustomAuthException thì lấy errorCode cụ thể
		// ==== NHÓM: TOKEN KHÁC ====
		if (ex instanceof AccessTokenExpiredException) {
			errorCode = SystemErrorCode.AUTH_EXPIRED_TOKEN;
			message = "Access token đã hết hạn.";
		} else if (ex instanceof AccessTokenBlacklistedException) {
			errorCode = SystemErrorCode.AUTH_TOKEN_BLACKLISTED;
			message = "Access token đã bị thu hồi hoặc không hợp lệ.";
		}else if (ex instanceof InvalidTokenTypeException) {
			errorCode = SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_TYP;
			message = "Token chứa type không hợp lệ.";
		} else if (ex instanceof InvalidJWTSignatureException) {
			errorCode = SystemErrorCode.AUTH_REFRESH_TOKEN_INVALID_SIGNATURE;
			message = "Token có chữ ký không hợp lệ.";
		}
		
		writeJsonResponse(response, status, errorCode, message, detailMessage, null);
	}
	
	// 403 Forbidden - Authenticated but access is denied (e.g., roles insufficient)
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
		String message = "Bạn không có quyền thực hiện hành động này.";
		String detailMessage = ex.toString();
		String errorCode = SystemErrorCode.AUTH_ACCESS_DENIED;
		HttpStatus status = HttpStatus.FORBIDDEN;
		appLogger.warn(request, "🛑 [{}] {} - {}", errorCode, message, ex.getMessage());
		
		writeJsonResponse(response, status, errorCode, message, detailMessage, null);
	}
}
