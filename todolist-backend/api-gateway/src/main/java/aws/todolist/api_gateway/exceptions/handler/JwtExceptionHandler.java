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
	
	
	@ExceptionHandler(AccessTokenBlacklistedException.class)
	public Mono<ResponseEntity<Object>> handleBlacklisted(
		ServerWebExchange exchange,
		AccessTokenBlacklistedException ex
	) {
		return buildErrorResponse(
			exchange,
			HttpStatus.FORBIDDEN, // 👈 khác 401
			ex.getErrorCode(),
			ex.getMessage(),
			ex,
			null
		);
	}
	
	
	@ExceptionHandler(GenericJwtException.class)
	public Mono<ResponseEntity<Object>> handleGenericJwtException(
		ServerWebExchange exchange,
		GenericJwtException ex
	) {
		return buildErrorResponse(
			exchange,
			HttpStatus.UNAUTHORIZED,
			ex.getErrorCode(),
			ex.getMessage(),
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
