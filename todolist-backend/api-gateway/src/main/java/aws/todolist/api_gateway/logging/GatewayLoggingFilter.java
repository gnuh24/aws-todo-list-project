package aws.todolist.api_gateway.logging;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GatewayLoggingFilter implements GlobalFilter, Ordered {

    private final AppLogger logger;

    public GatewayLoggingFilter(AppLogger logger) {
        this.logger = logger;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        long start = System.currentTimeMillis();

        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst("X-Trace-Id");
        String requestId = request.getHeaders().getFirst("X-Request-Id");

        if (traceId == null) traceId = UUID.randomUUID().toString().substring(0, 8);
        if (requestId == null) requestId = UUID.randomUUID().toString().substring(0, 8);

        MDC.put("traceId", traceId);
        MDC.put("requestId", requestId);

        logger.info("➡️ Incoming {} {} from {}", request.getMethod(), request.getURI(), request.getRemoteAddress());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - start;
                    int status = exchange.getResponse().getStatusCode() != null ?
                            exchange.getResponse().getStatusCode().value() : 0;

                    if (status >= 500) {
                        logger.error("Request {} {} -> status {} ({} ms)", request.getMethod(), request.getURI(), status, duration);
                    } else if (status >= 400) {
                        logger.warn("Request {} {} -> status {} ({} ms)", request.getMethod(), request.getURI(), status, duration);
                    } else {
                        logger.info("Request {} {} -> status {} ({} ms)", request.getMethod(), request.getURI(), status, duration);
                    }

                    MDC.clear();
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
