package aws.todolist.api_gateway.exceptions;

import aws.todolist.api_gateway.exceptions.JwtException.*;
import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {
	
	// ================== Token type không hợp lệ ==================
	@ExceptionHandler(InvalidTokenTypeException.class)
	public Mono<ResponseEntity<Object>> handleInvalidTokenType(ServerWebExchange exchange, InvalidTokenTypeException ex) {
		return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
		    SystemErrorCode.AUTH_TOKEN_INVALID_TYP,
		    "Token chứa type không hợp lệ",
		    ex, null);
	}
	
	// ================== Token hết hạn ==================
	@ExceptionHandler(TokenExpiredException.class)
	public Mono<ResponseEntity<Object>> handleExpiredToken(ServerWebExchange exchange, TokenExpiredException ex) {
		return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
		    SystemErrorCode.AUTH_EXPIRED_TOKEN,
		    "Access token đã hết hạn",
		    ex, null);
	}
	
	// ================== Chữ ký token không hợp lệ ==================
	@ExceptionHandler(InvalidJWTSignatureException.class)
	public Mono<ResponseEntity<Object>> handleInvalidSignature(ServerWebExchange exchange, InvalidJWTSignatureException ex) {
		return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
		    SystemErrorCode.AUTH_TOKEN_INVALID_SIGNATURE,
		    "Access token sai chữ ký",
		    ex, null);
	}
	
	// ================== Subject không tồn tại ==================
	@ExceptionHandler(UsernameNotFound.class)
	public Mono<ResponseEntity<Object>> handleUnknownSubject(ServerWebExchange exchange, UsernameNotFound ex) {
		return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
		    SystemErrorCode.AUTH_TOKEN_UNKNOWN_SUBJECT,
		    "Access token chứa subject không tồn tại",
		    ex, null);
	}
	
	// ================== Token missing / malformed / unsupported ==================
	@ExceptionHandler({MissingTokenException.class, MalformedJwtException.class, UnsupportedJwtException.class})
	public Mono<ResponseEntity<Object>> handleMalformedToken(ServerWebExchange exchange, Exception ex) {
		String code = SystemErrorCode.AUTH_MISSING_TOKEN; // Hoặc AUTH_TOKEN_MALFORMED / AUTH_TOKEN_UNSUPPORTED
		String message = "Token không hợp lệ hoặc thiếu";
		return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, code, message, ex, null);
	}
	
	// ================== Catch-all cho lỗi JWT khác ==================
	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<Object>> handleOtherJwtErrors(ServerWebExchange exchange, Exception ex) {
		return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
		    SystemErrorCode.AUTH_TOKEN_UNKNOWN_ERROR,
		    "Lỗi không xác định khi xử lý token",
		    ex, null);
	}
	
	private Mono<ResponseEntity<Object>> buildErrorResponse(
	    ServerWebExchange exchange,
	    HttpStatus status,
	    String code,
	    String message,
	    Exception ex,
	    Object data
	) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("code", code);
		body.put("message", message);
		body.put("path", exchange.getRequest().getURI().getPath());
		body.put("data", data);
		body.put("error", ex.getClass().getSimpleName());
		
		return Mono.just(ResponseEntity.status(status).body(body));
	}
}
