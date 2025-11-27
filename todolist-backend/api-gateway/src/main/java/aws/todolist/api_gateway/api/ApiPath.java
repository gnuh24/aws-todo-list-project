package aws.todolist.api_gateway.api;

import org.springframework.util.AntPathMatcher;

import java.util.List;

public class ApiPath {
	
	public static final String BASE = "/api";
	
	// ---------- AUTH ----------
	public static final String AUTH = BASE + "/auth";
	
	public static final String LOGIN = AUTH + "/v1/login";
	public static final String STAFF_LOGIN = AUTH + "/v1/staff-login";
	public static final String REGISTER = AUTH + "/v1/register";
	public static final String ACTIVE_ACCOUNT = AUTH + "/v1/active-account";
	public static final String CHECK_EMAIL = AUTH + "/v1/check-email";
	
	public static final String SEND_RESET_PASSWORD_OTP = AUTH + "/v1/send-reset-password-otp/**";
	public static final String RESET_PASSWORD = AUTH + "/v1/reset-password/**";
	
	public static final String SEND_UPDATE_EMAIL_OTP = AUTH + "/v1/send-update-email-otp/**";
	public static final String UPDATE_EMAIL = AUTH + "/v1/update-email";
	public static final String UPDATE_PASSWORD = AUTH + "/v1/update-password";
	public static final String REFRESH_TOKEN = AUTH + "/v1/refresh-token";

	
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
	    CHECK_EMAIL,
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
