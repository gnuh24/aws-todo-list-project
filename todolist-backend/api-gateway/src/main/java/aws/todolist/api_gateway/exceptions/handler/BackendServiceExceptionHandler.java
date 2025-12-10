package aws.todolist.api_gateway.exceptions.handler;

import aws.todolist.api_gateway.exceptions.errorCode.SystemErrorCode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import java.net.ConnectException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.web.reactive.function.client.WebClientRequestException;


@RestControllerAdvice
@Order(2)
public class BackendServiceExceptionHandler extends GlobalExceptionHandler {
	
	@ExceptionHandler(WebClientRequestException.class)
	public Mono<ResponseEntity<Object>> handleWebClient(ServerWebExchange exchange, WebClientRequestException ex) {
		return buildErrorResponse(exchange, HttpStatus.BAD_GATEWAY,
			SystemErrorCode.SYSTEM_UNKNOWN_ERROR,
			"Gateway không thể gọi backend (WebClient lỗi)",
			ex, null);
	}
	
	// ================== Backend không phản hồi (Connection refused) ==================
	@ExceptionHandler(ConnectException.class)
	public Mono<ResponseEntity<Object>> handleConnectionRefused(
		ServerWebExchange exchange,
		ConnectException ex
	) {
		
		// Lấy URL mà client gọi vào API Gateway
		String originalPath = exchange.getRequest().getURI().getPath();
		
		// Lấy message nguyên gốc từ Netty, có thể chứa hostname + port
		String backendTarget = extractBackendFromMessage(ex.getMessage());
		
		String message = "Không thể kết nối tới service backend";
		if (backendTarget != null) {
			message += ": " + backendTarget;
		}
		
		return buildErrorResponse(
			exchange,
			HttpStatus.SERVICE_UNAVAILABLE,
			SystemErrorCode.SYS_SERVICE_UNAVAILABLE,
			message,
			ex,
			null
		);
	}
	
	
	
	// ================== Timeout khi gọi backend ==================
	@ExceptionHandler(ReadTimeoutException.class)
	public Mono<ResponseEntity<Object>> handleReadTimeout(ServerWebExchange exchange, ReadTimeoutException ex) {
		return buildErrorResponse(exchange, HttpStatus.GATEWAY_TIMEOUT,
			SystemErrorCode.SYS_TIMEOUT,
			"Service phản hồi quá chậm",
			ex, null);
	}
	
	@ExceptionHandler(ConnectTimeoutException.class)
	public Mono<ResponseEntity<Object>> handleConnectTimeout(ServerWebExchange exchange, ConnectTimeoutException ex) {
		return buildErrorResponse(exchange, HttpStatus.GATEWAY_TIMEOUT,
			SystemErrorCode.SYS_TIMEOUT,
			"Không thể kết nối tới service đúng thời gian quy định",
			ex, null);
	}
	
	
	// ================== Service không tồn tại trong network / DNS ==================
	@ExceptionHandler({NoRouteToHostException.class, UnknownHostException.class})
	public Mono<ResponseEntity<Object>> handleServiceNotFound(ServerWebExchange exchange, Exception ex) {
		return buildErrorResponse(exchange, HttpStatus.SERVICE_UNAVAILABLE,
			SystemErrorCode.SYS_SERVICE_UNAVAILABLE,
			"Service không tồn tại hoặc không thể resolve hostname",
			ex, null);
	}
	
	
	// ================== SSL lỗi – rất hiếm (HTTPS communication error) ==================
	@ExceptionHandler(SSLHandshakeException.class)
	public Mono<ResponseEntity<Object>> handleSSL(ServerWebExchange exchange, SSLHandshakeException ex) {
		return buildErrorResponse(exchange, HttpStatus.BAD_GATEWAY,
			SystemErrorCode.SYSTEM_UNKNOWN_ERROR,
			"Lỗi SSL giữa Gateway và Backend",
			ex, null);
	}
	
	
	// ================== Catch–all cho lỗi hạ tầng ==================
	@ExceptionHandler(IOException.class)
	public Mono<ResponseEntity<Object>> handleIOError(ServerWebExchange exchange, IOException ex) {
		return buildErrorResponse(exchange, HttpStatus.BAD_GATEWAY,
			SystemErrorCode.SYSTEM_UNKNOWN_ERROR,
			"Lỗi IO giữa Gateway và Backend",
			ex, null);
	}
	
	
	private String extractBackendFromMessage(String msg) {
		if (msg == null) return null;
		
		// Tìm pattern dạng "xxx:port"
		Pattern p = Pattern.compile("([a-zA-Z0-9._-]+:\\d+)");
		Matcher m = p.matcher(msg);
		
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
}
