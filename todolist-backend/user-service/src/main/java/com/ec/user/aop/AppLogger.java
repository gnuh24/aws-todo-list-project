package com.ec.user.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AppLogger {
	
	private final Logger logger = LoggerFactory.getLogger("AppLogger");
	
	private String buildPrefix(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		String uri = request.getRequestURI();
		String username = "Anonymous";
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()) {
			username = auth.getName();
		}
		
		return String.format("[IP: %s] [URI: %s] [User: %s]", ip, uri, username);
	}
	
	public void info(HttpServletRequest request, String message, Object... args) {
		logger.info("{} - " + message, buildPrefix(request), args);
	}
	
	public void warn(HttpServletRequest request, String message, Object... args) {
		logger.warn("⚠️ {} - " + message, buildPrefix(request), args);
	}
	
	public void error(HttpServletRequest request, String message, Object... args) {
		logger.error("❌ {} - " + message, buildPrefix(request), args);
	}
	
	public void debug(HttpServletRequest request, String message, Object... args) {
		logger.debug("🐛 {} - " + message, buildPrefix(request), args);
	}
}
