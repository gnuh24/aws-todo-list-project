package aws.todolist.user.exceptionHandler.AuthException;

import aws.todolist.user.api.ApiResponse;
import aws.todolist.user.exceptionHandler.DetailError;
import aws.todolist.user.exceptionHandler.ErrorResponse;
import aws.todolist.user.exceptionHandler.errorCode.SystemErrorCode;
import aws.todolist.user.logging.AppLogger;
import aws.todolist.user.utils.EnvironmentUtils;
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
		writeJsonResponse(response, status, errorCode, message, detailMessage, null);
	}
	
	// 403 Forbidden - Authenticated but access is denied (e.g., roles insufficient)
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
		String message = "Bạn không có quyền thực hiện hành động này.";
		String detailMessage = ex.toString();
		String errorCode = SystemErrorCode.AUTH_ACCESS_DENIED;
		HttpStatus status = HttpStatus.FORBIDDEN;
		writeJsonResponse(response, status, errorCode, message, detailMessage, null);
	}
}
