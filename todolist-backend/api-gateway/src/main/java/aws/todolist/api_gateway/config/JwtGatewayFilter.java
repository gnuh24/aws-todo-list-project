package aws.todolist.api_gateway.config;

import aws.todolist.api_gateway.api.ApiPath;
import aws.todolist.api_gateway.exceptions.JwtException.InvalidJWTSignatureException;
import aws.todolist.api_gateway.exceptions.JwtException.InvalidTokenTypeException;
import aws.todolist.api_gateway.exceptions.JwtException.MissingTokenException;
import aws.todolist.api_gateway.exceptions.JwtException.TokenExpiredException;
import aws.todolist.api_gateway.utils.EnvironmentUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecureRequest;
import io.jsonwebtoken.security.SignatureException;
import org.springdoc.core.fn.builders.server.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter {
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private EnvironmentUtils environmentUtils;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String path = exchange.getRequest().getURI().getPath();
		
		System.err.println("Path: " + path);

		if (ApiPath.isPublicPath(path)) {
			System.err.println("No JWT -> Pass");
			System.err.println("_________________");
			
			return chain.filter(exchange);
		}
		
		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return Mono.error(new MissingTokenException("Missing Authorization header"));
		}
		
		String jwt = authHeader.substring(7);
		
		try {
			String type = jwtTokenProvider.getTokenType(jwt);
			if (!"access".equals(type)){
				return Mono.error(new InvalidTokenTypeException("Invalid token type"));
			}
			String email = jwtTokenProvider.getUsername(jwt);
			String userId = jwtTokenProvider.getAccountId(jwt);
			String role = jwtTokenProvider.getRole(jwt);
			
			ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
			    .header("X-User-Email", email)
			    .header("X-User-Id", userId)
			    .header("X-User-Role", role)
			    .header("X-Token-Type", type)
			    .build();
			
			System.err.println("Đã pass qua: " + email);
			return chain.filter(exchange.mutate().request(modifiedRequest).build());
			
		} catch (ExpiredJwtException e) {
			return Mono.error(new TokenExpiredException(environmentUtils.isDevMode() ? "Token đã hết hạn" : "Token expired"));
		} catch (SignatureException e) {
			return Mono.error(new InvalidJWTSignatureException(environmentUtils.isDevMode() ? "Chữ ký JWT không hợp lệ" : "Invalid JWT signature"));
		} catch (Exception e) {
			return Mono.error(new InvalidJWTSignatureException("Invalid JWT"));
		}
	}
	
}

