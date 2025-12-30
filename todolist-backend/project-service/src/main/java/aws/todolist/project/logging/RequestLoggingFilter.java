package aws.todolist.project.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final AppLogger logger;

    public RequestLoggingFilter(AppLogger logger) {
        this.logger = logger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // Gắn thông tin vào MDC để các logger khác có thể dùng
        MDC.put("traceId", traceId);
        MDC.put("requestId", requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();

            if (status >= 500) {
                logger.error("Request {} {} from {} -> status {} ({} ms)", method, uri, ip, status, duration);
            } else if (status >= 400) {
                logger.warn("Request {} {} from {} -> status {} ({} ms)", method, uri, ip, status, duration);
            } else {
                logger.info("Request {} {} from {} -> status {} ({} ms)", method, uri, ip, status, duration);
            }

            // Dọn MDC để tránh leak giữa các thread
            MDC.clear();
        }
    }
}