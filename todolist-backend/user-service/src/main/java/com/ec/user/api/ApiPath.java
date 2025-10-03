package com.ec.user.api;

import org.springframework.util.AntPathMatcher;

import java.util.List;

public class ApiPath {
	
	public static final String BASE = "/api/user";
	
	// ---------- AUTH ----------
	public static final String AUTH = BASE + "/auth";
	public static final String LOGIN = AUTH + "/login";
	public static final String STAFF_LOGIN = AUTH + "/staff-login";
	public static final String REGISTER = AUTH + "/register";
	public static final String ACTIVE_ACCOUNT = AUTH + "/active-account";
	public static final String CHECK_USERNAME = AUTH + "/check-username";
	
	public static final String SEND_RESET_PASSWORD_OTP = AUTH + "/send-reset-password-otp/**";
	public static final String RESET_PASSWORD = AUTH + "/reset-password/**";
	
	public static final String SEND_UPDATE_EMAIL_OTP = AUTH + "/send-update-email-otp/**";
	public static final String UPDATE_EMAIL = AUTH + "/update-email";
	public static final String UPDATE_PASSWORD = AUTH + "/update-password";
	public static final String REFRESH_TOKEN = AUTH + "/refresh-token";
	
	public static final String UPDATE_ROLE = AUTH + "/{id}/update-role";
	public static final String UPDATE_STATUS = AUTH + "/{id}/update-status";
	
	// ---------- ACCOUNTS ----------
	public static final String GET_ACCOUNT_BY_ID = BASE + "/accounts/{id}";
	public static final String GET_ACCOUNT_BY_EMAIL = BASE + "/accounts/email";
	public static final String CREATE_ACCOUNT = BASE + "/accounts";
	public static final String ACTIVATE_ACCOUNT = BASE + "/accounts/activate-account";
	public static final String ACCOUNT_ACTIVITY_LOG = BASE + "/accounts/{accountId}/account-activity-logs";
	public static final String UPDATE_ACCOUNT = BASE + "/accounts/{id}";
	public static final String UPDATE_ACCOUNT_PASSWORD = BASE + "/accounts/{id}/update-password";
	public static final String UPDATE_ACCOUNT_EMAIL = BASE + "/accounts/{id}/update-email";
	
	// ---------- PROFILES ----------
	public static final String PROFILE_ME = BASE + "/profiles/me";
	
	// ---------- ADDRESSES ----------
	public static final String ADDRESS_ME = BASE + "/addresses/me";
	public static final String ADDRESS_BY_ID = BASE + "/addresses/{addressId}";
	public static final String ADDRESS_SET_DEFAULT = BASE + "/addresses/{addressId}/set-default";
	
	// ---------- MEDIA ----------
	public static final String MEDIA_GET = BASE + "/media";
	public static final String MEDIA_UPLOAD = BASE + "/media/upload";
	
	// ---------- SWAGGER & DOCS ----------
	public static final String SWAGGER_UI = BASE + "/swagger/**";
	public static final String API_DOCS = BASE + "/v3/api-docs/**";
	
	// ---------- PUBLIC PATHS ----------
	private static final List<String> PUBLIC_PATH_PATTERNS = List.of(
	    LOGIN,
	    STAFF_LOGIN,
	    REGISTER,
	    CHECK_USERNAME,
	    ACTIVE_ACCOUNT,
	    SEND_RESET_PASSWORD_OTP,
	    RESET_PASSWORD,
	    REFRESH_TOKEN,
	    SWAGGER_UI,
	    API_DOCS
	);
	
	private static final AntPathMatcher matcher = new AntPathMatcher();
	
	public static boolean isPublicPath(String path) {
		return PUBLIC_PATH_PATTERNS.stream()
		    .anyMatch(pattern -> matcher.match(pattern, path));
	}
}
