package aws.todolist.api_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class OAuthRedirectPassThroughFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.defer(() -> {
            ServerHttpResponse response = exchange.getResponse();

            // Nếu backend trả về 302 redirect
            if (response.getStatusCode() == HttpStatus.FOUND) {
				System.err.println("Pass 302: ");
                // Để nguyên response, không viết thêm gì vào body
                return Mono.empty();
            }

            return Mono.empty();
        }));
    }

    @Override
    public int getOrder() {
        // Chọn thứ tự filter trước các filter ghi body / header
        return -1;
    }
}
