package aws.todolist.auth.security;

import aws.todolist.auth.logging.RequestLoggingFilter;
import aws.todolist.auth.exceptions.AuthException.AuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
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
	

	@Autowired
	@Lazy
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
					       CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
		    .csrf(AbstractHttpConfigurer::disable)
		    .cors(AbstractHttpConfigurer::disable)
		    
//		    .cors(cors -> cors.configurationSource(corsConfigurationSource))
		    .authorizeHttpRequests(auth -> auth
			// API public
			.requestMatchers(HttpMethod.GET, "/v1/check-username").permitAll()
			.requestMatchers(HttpMethod.POST, "/v1/login").permitAll()
			
			
			// Còn lại cần xác thực
			.anyRequest().permitAll()
		    )
		    .oauth2Login(oauth2 -> oauth2
			.successHandler(oAuth2LoginSuccessHandler)
		    )
		    
		    .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		    
		    // Logging filter nên nằm trước JWT filter
		    .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
		    
		    .exceptionHandling(exception -> exception
			.authenticationEntryPoint(authExceptionHandler)
			.accessDeniedHandler(authExceptionHandler)
		    );
		
		
		return http.build();
	}
	
	
}
