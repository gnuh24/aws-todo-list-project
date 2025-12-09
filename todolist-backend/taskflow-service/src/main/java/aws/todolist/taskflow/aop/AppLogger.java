package aws.todolist.taskflow.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class AppLogger {

    private static final Logger logger = LoggerFactory.getLogger("AppLogger");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String buildPrefix() {
	    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
		.currentRequestAttributes())
		.getRequest();
	    
	    String username = request.getHeader("X-User-Email");
	    if (username == null) username = "Anonymous";


        String traceId = MDC.get("traceId");
        String requestId = MDC.get("requestId");

        String timestamp = LocalDateTime.now().format(formatter);

        return String.format("[Time: %s] [User: %s] [Trace: %s] [Req: %s]", timestamp, username, traceId, requestId);
    }

    private Object[] withPrefix(Object... args) {
        return Stream.concat(Stream.of(buildPrefix()), Arrays.stream(args)).toArray();
    }

    public void info(String message, Object... args) {
        logger.info("{} - " + message, withPrefix(args));
    }

    public void warn(String message, Object... args) {
        logger.warn("⚠️ {} - " + message, withPrefix(args));
    }

    public void error(String message, Object... args) {
        logger.error("❌ {} - " + message, withPrefix(args));
    }

    public void debug(String message, Object... args) {
        logger.debug("🐛 {} - " + message, withPrefix(args));
    }
}