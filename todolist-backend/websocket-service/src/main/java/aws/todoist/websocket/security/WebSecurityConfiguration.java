package aws.todoist.websocket.security;

import aws.todolist.notification.exceptions.AuthException.AuthExceptionHandler;
import aws.todolist.notification.logging.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
	
	@Autowired
	@Lazy
	private AuthExceptionHandler authExceptionHandler;
	
	@Autowired
	private RequestLoggingFilter requestLoggingFilter;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
					       CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
		    // Loại bỏ bảo vệ CSRF
		    .csrf(AbstractHttpConfigurer::disable)
		    .cors(AbstractHttpConfigurer::disable)
		    
		    // Configure các luồng truy cập
		    .authorizeHttpRequests(auth -> auth
			    
			    // Xác thực tất cả các request

			    
			    // PermitAll cho các API public
			    .requestMatchers(HttpMethod.GET, "/v1/check-username").permitAll()
			    .requestMatchers(HttpMethod.POST, "/v1/login").permitAll()
			    .requestMatchers(HttpMethod.POST, "/v1/staff-login").permitAll()
			    .requestMatchers(HttpMethod.POST, "/v1/register").permitAll()
			    .requestMatchers(HttpMethod.POST, "/v1/active-account").permitAll()
			    
			    .requestMatchers(HttpMethod.POST, "/v1/send-reset-password-otp/{username}").permitAll()
			    .requestMatchers(HttpMethod.PATCH, "/v1/reset-password/{username}").permitAll()
			    
			    .requestMatchers(HttpMethod.POST, "/v1/send-update-email-otp/{username}").hasAnyAuthority("USER")
			    .requestMatchers(HttpMethod.PATCH, "/v1/update-email").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.GET, "/auth/update-email").hasAnyAuthority("USER")
			    
			    
			    .requestMatchers(HttpMethod.PATCH, "/v1/update-password").hasAnyAuthority("USER")
			    .requestMatchers(HttpMethod.POST, "/v1/refresh-token").permitAll()
			
			    
			    // Còn lại cần xác thực
//			    .anyRequest().authenticated()
			
			    .anyRequest().permitAll()
		    
		    
		    
		    ).httpBasic(Customizer.withDefaults())
		    
		    // Add JWT vào chuỗi lọc và ưu tiên loc theo JWT
		    .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		    
		    // Logging filter nên nằm trước JWT filter
		    .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
		    
		    .exceptionHandling((exceptionHandling) -> exceptionHandling
			
			// Cấu hình xử lý ngoại lệ cho trường hợp không xác thực (Login sai ^^)
			.authenticationEntryPoint(authExceptionHandler)
			
			// Cấu hình xử lý ngoại lệ cho trường hợp truy cập bị từ chối (Không đủ quyền)
			.accessDeniedHandler(authExceptionHandler)
		    
		    );
		
		return http.build();
	}
	
	
}
