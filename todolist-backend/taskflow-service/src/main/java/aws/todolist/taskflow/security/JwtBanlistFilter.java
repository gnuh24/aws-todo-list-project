//package com.ec.user.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtBanlistFilter  extends OncePerRequestFilter {
//
//	// ✅ Danh sách các đường dẫn public bắt đầu bằng /api/user
//	private boolean isPublicPath(String path) {
//		return path.startsWith("/api/user/auth/login")
//		    || path.startsWith("/api/user/auth/register")
//		    || path.startsWith("/api/user/auth/check-username")
//		    || path.startsWith("/api/user/auth/staff-login")
//		    || path.startsWith("/api/user/auth/active-account")
//		    || path.startsWith("/api/user/auth/send-reset-password-otp")
//		    || path.startsWith("/api/user/auth/reset-password")
//		    || path.startsWith("/api/user/swagger")
//		    || path.startsWith("/api/user/v3/api-docs");
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request,
//					HttpServletResponse response,
//					FilterChain filterChain) throws ServletException, IOException {
//
//	}
//}
