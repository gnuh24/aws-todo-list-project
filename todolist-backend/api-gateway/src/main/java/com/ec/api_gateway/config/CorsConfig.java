//package com.ec.api_gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//
//import java.util.List;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//	CorsConfiguration config = new CorsConfiguration();
//	config.setAllowedOriginPatterns(List.of("*")); // Hoặc domain cụ thể như "http://localhost:3000"
//	config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//	config.setAllowedHeaders(List.of("*"));
//	config.setAllowCredentials(true);
//
//	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	source.registerCorsConfiguration("/**", config);
//
//	return new CorsWebFilter((CorsConfigurationSource) source);
//    }
//
//}
//
