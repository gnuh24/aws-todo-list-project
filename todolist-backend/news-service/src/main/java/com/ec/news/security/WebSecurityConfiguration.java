package com.ec.news.security;

import com.ec.news.exceptions.AuthException.AuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
//
//			.requestMatchers(HttpMethod.PATCH, "/accounts/{id}")                                            .hasAnyAuthority("USER")
//			.requestMatchers(HttpMethod.PATCH, "/accounts/{id}/update-password")                .hasAnyAuthority("USER")
//			.requestMatchers(HttpMethod.PATCH, "/accounts/{id}/update-email")                       .hasAnyAuthority("USER")
//
//
//			.requestMatchers( HttpMethod.GET, "/media")                                                           .permitAll()
//			.requestMatchers( HttpMethod.POST, "/media/upload")                                             .permitAll()
//
//
//			.requestMatchers(HttpMethod.POST, "/auth/send-otp-update-email")                        .hasAnyAuthority("USER")
//			.requestMatchers(HttpMethod.POST, "/auth/send-otp-reset-password")                    .permitAll()
//			.requestMatchers(HttpMethod.PATCH, "/auth/{id}/update-role")                                 .hasAnyAuthority("ADMIN")
//			.requestMatchers(HttpMethod.PATCH, "/auth/{id}/update-status")                              .hasAnyAuthority("ADMIN")
			    
//			    // PermitAll cho các API public
//			    .requestMatchers(HttpMethod.GET, "/auth/check-username").permitAll()
//			    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
//			    .requestMatchers(HttpMethod.POST, "/auth/staff-login").permitAll()
//			    .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
//			    .requestMatchers(HttpMethod.POST, "/auth/active-account").permitAll()
//
//			    .requestMatchers(HttpMethod.POST, "/auth/send-reset-password-otp/{username}").permitAll()
//			    .requestMatchers(HttpMethod.PATCH, "/auth/reset-password/{username}").permitAll()
//
//			    .requestMatchers(HttpMethod.POST, "/auth/send-update-email-otp/{username}").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.PATCH, "/auth/update-email").hasAnyAuthority("USER")
//
//
//			    .requestMatchers(HttpMethod.PATCH, "/auth/update-password").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.POST, "/auth/refresh-token").permitAll()
//
//			    .requestMatchers(HttpMethod.GET, "/profiles/me").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.PATCH, "/profiles/me").hasAnyAuthority("USER")
//
//
//			    .requestMatchers(HttpMethod.GET, "/addresses/me").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.POST, "/addresses/me").hasAnyAuthority("USER")
//
//			    .requestMatchers(HttpMethod.PATCH, "/addresses/{addressId}").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.PATCH, "/addresses/{addressId}/set-default").hasAnyAuthority("USER")
//			    .requestMatchers(HttpMethod.DELETE, "/addresses/{addressId}").hasAnyAuthority("USER")
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
