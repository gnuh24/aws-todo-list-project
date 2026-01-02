package aws.todolist.chat.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    // Lấy secret từ application.yaml
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0).substring(7); // Bỏ "Bearer "

                try {
                    // 1. Decode Secret Key theo chuẩn Base64 (Giống hệt logic Gateway)
                    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
                    SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");

                    // 2. Cấu hình Decoder
                    JwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                            .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256) // Gateway dùng HmacSHA256 tương ứng HS256
                            .build();

                    // 3. Verify & Parse
                    Jwt jwt = jwtDecoder.decode(token);
                    String userId = jwt.getSubject(); // Lấy subject (username/id)

                    // 4. Set User
                    accessor.setUser(() -> userId);

                    log.info("User {} connected via Gateway", userId);

                } catch (Exception e) {
                    log.error("WebSocket Auth Failed: {}", e.getMessage());
                    throw new IllegalArgumentException("Unauthorized: Invalid Token");
                }
            } else {
                 throw new IllegalArgumentException("Unauthorized: No Token");
            }
        }
        return message;
    }
}