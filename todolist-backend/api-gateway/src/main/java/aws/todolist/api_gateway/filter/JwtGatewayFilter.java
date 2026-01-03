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
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter {
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private ReactiveRedisTemplate<String, String> redisTemplate;
	@Autowired
	private RedisService redisService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String path = exchange.getRequest().getURI().getPath();

		// --- DEBUG LOG ---
		System.out.println("=================================================");
		System.out.println("🔍 GATEWAY CHECK PATH: " + path);
		System.out.println("=================================================");

		// --- 🟢 FIX FINAL: Chấp nhận path gốc HOẶC path sau khi StripPrefix ---
		// Thêm điều kiện: path.startsWith("/ws/")
		if (ApiPath.isPublicPath(path) ||
				path.contains("/chat/") ||
				path.startsWith("/ws/")) {

			System.out.println("✅ ALLOWED (Public/Chat/WS): " + path);
			return chain.filter(exchange);
		}
		// ---------------------------------------------------------------------

		System.err.println("🔒 Checking JWT for: " + path);

		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			System.err.println("❌ Missing Token for: " + path);
			return Mono.error(new MissingTokenException());
		}

		String jwt = authHeader.substring(7);

		try {
			// ... (Giữ nguyên phần logic check token cũ của bạn ở dưới) ...
			String type = jwtTokenProvider.getTokenType(jwt);
			if (!"access".equals(type)){
				return Mono.error(new InvalidTokenTypeException());
			}

			String email  = jwtTokenProvider.getUsername(jwt);
			String userId = jwtTokenProvider.getAccountId(jwt);
			String role   = jwtTokenProvider.getRole(jwt);

			String redisKey = RedisConstants.BANLIST_ACCOUNT_ID + ":" + userId;

			return redisService.get(redisKey)
					.flatMap(optionalValue -> {
						if (optionalValue.isPresent()) {
							return Mono.error(new AccessTokenBlacklistedException());
						}
						ServerHttpRequest modified = exchange.getRequest().mutate()
								.header("X-User-Email", email)
								.header("X-User-Id", userId)
								.header("X-User-Role", role)
								.header("X-Token-Type", type)
								.build();

						return chain.filter(exchange.mutate().request(modified).build());
					})
					.onErrorResume(ex -> Mono.error(ex));

		} catch (ExpiredJwtException e) {
			return Mono.error(new TokenExpiredException());
		} catch (SignatureException e) {
			return Mono.error(new InvalidJWTSignatureException());
		} catch (MalformedJwtException | IllegalArgumentException e) {
			return Mono.error(new MalformedTokenException());
		} catch (UnsupportedJwtException e) {
			return Mono.error(new UnsupportedTokenException());
		} catch (JwtException e) {
			return Mono.error(new TokenUnknownErrorException());
		}
	}
	
	
}

