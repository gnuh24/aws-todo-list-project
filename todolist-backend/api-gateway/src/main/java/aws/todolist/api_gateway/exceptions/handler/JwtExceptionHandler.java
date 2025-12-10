package aws.todolist.api_gateway.exceptions.handler;

import aws.todolist.api_gateway.exceptions.JwtException.*;
import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;
import io.jsonwebtoken.JwtException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(1)
public class JwtExceptionHandler {
	
	// ================== Token thiếu ==================
	@ExceptionHandler(MissingTokenException.class)
	public Mono<ResponseEntity<Object>> handleMissingToken(ServerWebExchange exchange, MissingTokenException ex) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			SystemErrorCode.AUTH_MISSING_TOKEN,
			"Thiếu Authorization header hoặc token",
			ex,
			null
		);
	}
	
	// ================== Token malformed ==================
	@ExceptionHandler(MalformedTokenException.class)
	public Mono<ResponseEntity<Object>> handleMalformedToken(ServerWebExchange exchange, MalformedTokenException ex) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			SystemErrorCode.AUTH_TOKEN_MALFORMED,
			"Token không đúng định dạng hoặc bị lỗi",
			ex,
			null
		);
	}
	
	// ================== Token unsupported ==================
	@ExceptionHandler(UnsupportedTokenException.class)
	public Mono<ResponseEntity<Object>> handleUnsupportedToken(ServerWebExchange exchange, UnsupportedTokenException ex) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			SystemErrorCode.AUTH_TOKEN_UNSUPPORTED,
			"Thuật toán hoặc định dạng token không được hỗ trợ",
			ex,
			null
		);
	}
	
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
	
	@ExceptionHandler(AccessTokenBlacklistedException.class)
	public Mono<ResponseEntity<Object>> handleBlacklistedToken(
		ServerWebExchange exchange,
		AccessTokenBlacklistedException ex
	) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			SystemErrorCode.AUTH_TOKEN_BLACKLISTED, // SYS-AUTH-008
			"Token đã bị thu hồi hoặc không còn hợp lệ",
			ex,
			null
		);
	}
	
	
	
	// ================== Catch–all cho lỗi JWT không xác định ==================
	@ExceptionHandler(GenericJwtException.class)
	public Mono<ResponseEntity<Object>> handleGenericJwtError(ServerWebExchange exchange, GenericJwtException ex) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			SystemErrorCode.AUTH_TOKEN_UNKNOWN_ERROR,
			"Lỗi JWT không xác định",
			ex,
			null
		);
	}
	
	@ExceptionHandler(JwtException.class)
	public Mono<ResponseEntity<Object>> handleGenericJwtError(ServerWebExchange exchange, JwtException ex) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			SystemErrorCode.AUTH_TOKEN_UNKNOWN_ERROR,
			"Lỗi JWT không xác định",
			ex,
			null
		);
	}
	
	/* =============================
	 *       RESPONSE BUILDER
	 * ============================= */
	protected final Mono<ResponseEntity<Object>> buildErrorResponse(
		ServerWebExchange exchange,
		HttpStatus status,
		String code,
		String message,
		Exception ex,
		Object data) {
		
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("code", code);
		body.put("message", message);
		body.put("path", exchange.getRequest().getURI().getPath());
		body.put("error", ex.getClass().getSimpleName());
		body.put("data", data);
		
		return Mono.just(
			ResponseEntity.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body)
		);
	}
}
