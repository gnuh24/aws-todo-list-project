package aws.todolist.api_gateway.filter;

import aws.todolist.api_gateway.api.ApiPath;
import aws.todolist.api_gateway.config.JwtTokenProvider;
import aws.todolist.api_gateway.exceptions.JwtException.*;
import aws.todolist.api_gateway.integration.redis.RedisConstants;
import aws.todolist.api_gateway.integration.redis.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
	private RedisService redisService;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		String path = exchange.getRequest().getURI().getPath();
		
		if (ApiPath.isPublicPath(path)) {
			return chain.filter(exchange);
		}
		System.err.println("Activate JWT Security with: " + path);
		
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
			
			String email  = jwtTokenProvider.getUsername(jwt);
			String userId = jwtTokenProvider.getAccountId(jwt);
			String role   = jwtTokenProvider.getRole(jwt);
			
			// Check trong blacklist
//			System.err.println("KEY: " + RedisConstants.BANLIST_ACCOUNT_ID + ":" + userId);
//			Object redisValue = redisService.get(RedisConstants.BANLIST_ACCOUNT_ID + ":" + userId).toString();
//			System.err.println("Value = " + redisValue);
//
//			if(redisService.exists(RedisConstants.BANLIST_ACCOUNT_ID + ":" + userId)){
//				return Mono.error(new AccessTokenBlacklistedException("Invalid token type"));
//			}
			
			ServerHttpRequest modified = exchange.getRequest().mutate()
				.header("X-User-Email", email)
				.header("X-User-Id", userId)
				.header("X-User-Role", role)
				.header("X-Token-Type", type)
				.build();
			
			return chain.filter(exchange.mutate().request(modified).build());
			
		} catch (ExpiredJwtException e) {
			return Mono.error(new TokenExpiredException("Token đã hết hạn"));
		} catch (SignatureException e) {
			return Mono.error(new InvalidJWTSignatureException("Chữ ký JWT không hợp lệ"));
		} catch (MalformedJwtException e) {
			return Mono.error(new MalformedTokenException("Token sai cấu trúc"));
		} catch (UnsupportedJwtException e) {
			return Mono.error(new UnsupportedTokenException("Thuật toán không hỗ trợ"));
		} catch (IllegalArgumentException e) {
			return Mono.error(new MalformedTokenException("Token rỗng hoặc sai định dạng"));
		} catch (JwtException e) {
			return Mono.error(new GenericJwtException("Lỗi JWT không xác định"));
		}
	}
	
	
}

