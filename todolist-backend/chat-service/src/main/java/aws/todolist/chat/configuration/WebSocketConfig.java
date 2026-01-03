package aws.todolist.chat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor; // Inject Interceptor

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // QUAN TRỌNG: Dùng setAllowedOriginPatterns("*")
                // để cho phép Gateway forward request từ bất kỳ đâu vào
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. Kích hoạt Simple Broker cho cả /topic (public) và /queue (private)
        // QUAN TRỌNG: Phải có "/queue" vì Controller đang gửi vào đường dẫn này
        registry.enableSimpleBroker("/topic", "/queue");

        // 2. Prefix tin nhắn từ Client gửi lên Server
        registry.setApplicationDestinationPrefixes("/app");

        // 3. Prefix dành cho User Destination (Spring tự động xử lý cái này)
        // Khi client sub vào /user/queue/..., Spring sẽ map vào đúng session của user đó
        registry.setUserDestinationPrefix("/user");
    }

    // --- THÊM ĐOẠN NÀY ---
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Đăng ký interceptor để check token trước khi xử lý message
        registration.interceptors(webSocketAuthInterceptor);
    }
}
