package com.ec.order.security;


import com.ec.order.exceptions.AuthException.AuthExceptionHandler;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

//	@Autowired
//	@Lazy
//	private AccountService accountService;
	
	@Autowired
	@Lazy
	private AuthExceptionHandler authExceptionHandler;
	
	@Autowired
	private JwtTokenFilter jwtAuthFIlter;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:4200"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
					       CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
		    // Loại bỏ bảo vệ CSRF
		    .csrf(AbstractHttpConfigurer::disable)
		    .cors(cors -> cors.configurationSource(corsConfigurationSource))
		    
		    
		    // Configure các luồng truy cập
		    .authorizeHttpRequests(auth -> auth
			    
			    // Xác thực tất cả các request
//			.requestMatchers(HttpMethod.GET, "/accounts/{Id}")                                                .permitAll()
//			.requestMatchers(HttpMethod.GET, "/accounts/email")                                             .permitAll()
//
//			.requestMatchers(HttpMethod.POST, "/accounts")                                                    .permitAll()
//			.requestMatchers(HttpMethod.POST, "/accounts/activate-account")                         .permitAll()
//			.requestMatchers(HttpMethod.POST, "/accounts/{accountId}/account-activity-logs").hasAnyAuthority("USER")

//
			    // Còn lại cần xác thực
//			    .anyRequest().authenticated()
			
			    .anyRequest().permitAll()
		    
		    
		    
		    ).httpBasic(Customizer.withDefaults())
		    
		    // Add JWT vào chuỗi lọc và ưu tiên loc theo JWT
		    .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		    
		    .addFilterBefore(jwtAuthFIlter, UsernamePasswordAuthenticationFilter.class)
		    
		    .exceptionHandling((exceptionHandling) -> exceptionHandling
			
			// Cấu hình xử lý ngoại lệ cho trường hợp không xác thực (Login sai ^^)
			.authenticationEntryPoint(authExceptionHandler)
			
			// Cấu hình xử lý ngoại lệ cho trường hợp truy cập bị từ chối (Không đủ quyền)
			.accessDeniedHandler(authExceptionHandler)
		    
		    );
		
		return http.build();
	}
	
	
}
