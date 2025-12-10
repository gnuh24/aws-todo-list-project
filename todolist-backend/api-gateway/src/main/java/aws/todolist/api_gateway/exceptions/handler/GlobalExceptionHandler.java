package aws.todolist.api_gateway.exceptions.handler;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Order(99)
public class GlobalExceptionHandler {
	
	/* =============================
	 *        API / HTTP ERRORS
	 * ============================= */
	
	// 404 – Không tìm thấy API
	@ExceptionHandler(ResponseStatusException.class)
	private Mono<ResponseEntity<Object>> handleNotFound(ServerWebExchange exchange, ResponseStatusException ex) {
		if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
			return buildErrorResponse(exchange, HttpStatus.NOT_FOUND,
				SystemErrorCode.API_NOT_FOUND,
				"API không tồn tại",
				ex, null);
		}
		return handleAll(ex, exchange);
	}
	
	// 405 – Method không hỗ trợ
	@ExceptionHandler(MethodNotAllowedException.class)
	private Mono<ResponseEntity<Object>> handleMethodNotAllowed(ServerWebExchange exchange, MethodNotAllowedException ex) {
		return buildErrorResponse(exchange, HttpStatus.METHOD_NOT_ALLOWED,
			SystemErrorCode.API_METHOD_NOT_ALLOWED,
			"Phương thức HTTP không được hỗ trợ",
			ex, null);
	}
	
	// 415 – Media type không hỗ trợ
	@ExceptionHandler(UnsupportedMediaTypeStatusException.class)
	private Mono<ResponseEntity<Object>> handleUnsupportedMediaType(ServerWebExchange exchange, UnsupportedMediaTypeStatusException ex) {
		return buildErrorResponse(exchange, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
			SystemErrorCode.API_UNSUPPORTED_MEDIA_TYPE,
			"Loại dữ liệu không được hỗ trợ",
			ex, null);
	}
	
	// 406 – Không chấp nhận loại response
	@ExceptionHandler(NotAcceptableStatusException.class)
	private Mono<ResponseEntity<Object>> handleNotAcceptable(ServerWebExchange exchange, NotAcceptableStatusException ex) {
		return buildErrorResponse(exchange, HttpStatus.NOT_ACCEPTABLE,
			SystemErrorCode.API_NOT_ACCEPTABLE,
			"Không chấp nhận loại phản hồi",
			ex, null);
	}
	
	// 400 – Dữ liệu request không hợp lệ
	@ExceptionHandler(ServerWebInputException.class)
	private Mono<ResponseEntity<Object>> handleBadRequest(ServerWebExchange exchange, ServerWebInputException ex) {
		return buildErrorResponse(exchange, HttpStatus.BAD_REQUEST,
			SystemErrorCode.API_BAD_REQUEST,
			"Request không hợp lệ",
			ex, null);
	}
	
	/* =============================
	 *         VALIDATION ERRORS
	 * ============================= */
	@ExceptionHandler(IllegalArgumentException.class)
	private Mono<ResponseEntity<Object>> handleInvalidArgument(ServerWebExchange exchange, IllegalArgumentException ex) {
		return buildErrorResponse(exchange, HttpStatus.BAD_REQUEST,
			SystemErrorCode.SYS_INVALID_FORMAT,
			"Tham số không hợp lệ",
			ex, null);
	}
	
	
	// Backend timeout
	@ExceptionHandler(TimeoutException.class)
	private Mono<ResponseEntity<Object>> handleTimeout(ServerWebExchange exchange, TimeoutException ex) {
		return buildErrorResponse(exchange, HttpStatus.GATEWAY_TIMEOUT,
			SystemErrorCode.SYS_TIMEOUT,
			"Backend không phản hồi (timeout)",
			ex, null);
	}
	
	/* =============================
	 *       FILE / UPLOAD ERRORS
	 * ============================= */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	private Mono<ResponseEntity<Object>> handleFileTooLarge(ServerWebExchange exchange, MaxUploadSizeExceededException ex) {
		return buildErrorResponse(exchange, HttpStatus.PAYLOAD_TOO_LARGE,
			SystemErrorCode.SYS_FILE_TOO_LARGE,
			"File vượt quá dung lượng cho phép",
			ex, null);
	}
	
	
	
	/* =============================
	 *        CATCH-ALL (DEFAULT)
	 * ============================= */
	@ExceptionHandler(Exception.class)
	private Mono<ResponseEntity<Object>> handleAll(Exception ex, ServerWebExchange exchange) {
		return buildErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
			SystemErrorCode.SYS_INTERNAL_SERVER_ERROR,
			"Lỗi không xác định",
			ex, null);
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
