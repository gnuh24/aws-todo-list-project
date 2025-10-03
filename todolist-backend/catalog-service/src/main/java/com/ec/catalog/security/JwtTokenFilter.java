package com.ec.catalog.security;

import com.ec.catalog.aop.AppLogger;
import com.ec.catalog.api.ApiPath;
import com.ec.catalog.exceptions.AuthException.AuthExceptionHandler;
import com.ec.catalog.exceptions.JwtException.InvalidJWTSignatureException;
import com.ec.catalog.exceptions.JwtException.InvalidTokenTypeException;
import com.ec.catalog.exceptions.JwtException.TokenExpiredException;
import com.ec.catalog.exceptions.JwtException.UsernameNotFound;
import com.ec.catalog.service.AccountService;
import com.ec.catalog.utils.EnvironmentUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	@Lazy
	private AccountService accountService;
	
	@Autowired
	private AuthExceptionHandler authExceptionHandler;
	
	@Autowired
	private AppLogger log;
	
	@Autowired
	private EnvironmentUtils environmentUtils;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
					@NonNull HttpServletResponse response,
					@NonNull FilterChain filterChain) throws ServletException, IOException {
		
		final String path = request.getRequestURI();
		
		// ✅ Bỏ qua filter nếu là API public
		if (ApiPath.isPublicPath(path)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		final String authHeader = request.getHeader("Authorization");
		final String jwtToken;
		final String userEmail;
		String errorString = "Token không hợp lệ hoặc đã hết hạn sử dụng.";
		
		if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			jwtToken = authHeader.substring(7);
			
			try {
				String typeToken = jwtTokenProvider.getTokenType(jwtToken);
				if (typeToken == null || !typeToken.equals("access")) {
					throw new InvalidTokenTypeException("Access token có type không hợp lệ.");
				}
				
				userEmail = jwtTokenProvider.getUsername(jwtToken);
				
				if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = accountService.loadUserByUsername(userEmail);
					
					UsernamePasswordAuthenticationToken authToken =
					    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContext context = SecurityContextHolder.createEmptyContext();
					context.setAuthentication(authToken);
					SecurityContextHolder.setContext(context);
					
					log.info(request, "✅ Token hợp lệ. Đã xác thực người dùng: {}", userEmail);
				}
				
			} catch (ExpiredJwtException e) {
				if (environmentUtils.isDevMode()) {
					errorString = "Token đã hết hạn. ";
				}
				authExceptionHandler.commence(request, response, new TokenExpiredException(errorString));
				
				return;
			} catch (SignatureException e) {
				if (environmentUtils.isDevMode()) {
					errorString = "Chữ ký JWT không hợp lệ.";
				}
				authExceptionHandler.commence(request, response, new InvalidJWTSignatureException(errorString));
				
				return;
			} catch (UsernameNotFoundException e) {
				if (environmentUtils.isDevMode()) {
					errorString = "Token chứa thông tin không tồn tại.";
				}
				authExceptionHandler.commence(request, response, new UsernameNotFound(errorString));
				
				return;
			} catch (InvalidTokenTypeException e) {
				if (environmentUtils.isDevMode()) {
					errorString = "Access Token chứa type không đúng.";
				}
				authExceptionHandler.commence(request, response, new InvalidTokenTypeException(errorString));
				return;
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
