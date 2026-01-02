package aws.todolist.chat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Client kết nối vào endpoint này: http://localhost:8089/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // Cho phép mọi nguồn (CORS)
                .withSockJS();  // Hỗ trợ fallback nếu trình duyệt không có WebSocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. Client muốn nhận tin nhắn thì subscribe vào các prefix này
        // /topic: Chat public (Group)
        // /user: Chat private (1-1)
        registry.enableSimpleBroker("/topic", "/user");

        // 3. Client muốn gửi tin nhắn thì gửi vào prefix này: /app/chat
        registry.setApplicationDestinationPrefixes("/app");

        // 4. Prefix dành cho user cụ thể (mặc định là /user)
        registry.setUserDestinationPrefix("/user");
    }
}
