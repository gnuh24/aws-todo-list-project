package aws.todolist.chat.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Base64;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@Configuration
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    // Đảm bảo key này GIỐNG HỆT auth-service và gateway
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Chỉ kiểm tra khi Client gửi lệnh CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // 1. Lấy Token từ Header 'Authorization' của gói tin STOMP
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    // 2. Parse Token
                    byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

                    Claims claims = Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    // Lấy accountId (UUID) từ payload
                    String userId = claims.get("accountId", String.class);
                    if (userId == null) {
                        System.err.println("❌ Error: userId is null");
                    }

                    // 3. Set Authentication vào Context để Spring biết user này là ai
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                    accessor.setUser(authentication);
                    log.info("✅ User connected: {}", userId);

                } catch (Exception e) {
                    log.error("❌ Invalid Token: {}", e.getMessage());
                    // Có thể throw exception để từ chối kết nối
                }
            } else {
                log.warn("⚠️ No Token provided in WebSocket connection!");
            }
        }
        return message;
    }
}