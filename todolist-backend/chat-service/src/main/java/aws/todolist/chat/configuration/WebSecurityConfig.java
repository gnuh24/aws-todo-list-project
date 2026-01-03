package aws.todolist.chat.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable) // Tắt CORS ở mức Security (đã xử lý ở WebSocketConfig)
                .authorizeHttpRequests(auth -> auth
                        // QUAN TRỌNG: Cho phép bắt tay WebSocket mà không cần Token ở HTTP Header
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}