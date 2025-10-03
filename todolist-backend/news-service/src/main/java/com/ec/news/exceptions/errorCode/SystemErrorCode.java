package com.ec.news.exceptions.errorCode;

public final class SystemErrorCode {

    private SystemErrorCode() {
        // Prevent instantiation
    }
    
	
	// ==== AUTHENTICATION / TOKEN ====
	public static final String AUTH_MISSING_TOKEN             					= "SYS-AUTH-001";  // Thiếu token xác thực
	public static final String AUTH_EXPIRED_TOKEN            					= "SYS-AUTH-002";  // Access token đã hết hạn
	public static final String AUTH_TOKEN_INVALID_SIGNATURE   			= "SYS-AUTH-003";  // Access token sai chữ ký
	public static final String AUTH_TOKEN_INVALID_TYP         				= "SYS-AUTH-004";  // Access token chứa typ không hợp lệ
	public static final String AUTH_TOKEN_UNKNOWN_SUBJECT     			= "SYS-AUTH-005";  // Access token chứa subject không tồn tại
	public static final String AUTH_TOKEN_MALFORMED           				= "SYS-AUTH-006";  // Access token không đúng định dạng
	public static final String AUTH_TOKEN_UNSUPPORTED         				= "SYS-AUTH-007";  // Access token sử dụng thuật toán không được hỗ trợ
	public static final String AUTH_TOKEN_BLACKLISTED         				= "SYS-AUTH-008";  // Access token đã bị thu hồi hoặc nằm trong blacklist
	public static final String AUTH_TOKEN_UNKNOWN_ERROR       			= "SYS-AUTH-009";  // Lỗi không xác định khi xử lý token
	
	// ==== REFRESH TOKEN ====
	public static final String AUTH_MISSING_REFRESH_TOKEN             		= "SYS-AUTH-010"; // Thiếu refresh token
	public static final String AUTH_REFRESH_TOKEN_EXPIRED            			= "SYS-AUTH-011"; // Refresh token đã hết hạn
	public static final String AUTH_REFRESH_TOKEN_INVALID_SIGNATURE  	= "SYS-AUTH-012"; // Refresh token sai chữ ký
	public static final String AUTH_REFRESH_TOKEN_INVALID_TYP        		= "SYS-AUTH-013"; // Refresh token chứa typ không hợp lệ
	public static final String AUTH_REFRESH_TOKEN_UNKNOWN_SUBJECT    	= "SYS-AUTH-014"; // Refresh token chứa subject không tồn tại
	public static final String AUTH_REFRESH_TOKEN_MALFORMED         		= "SYS-AUTH-015"; // Refresh token không đúng định dạng
	public static final String AUTH_REFRESH_TOKEN_UNSUPPORTED        		= "SYS-AUTH-016"; // Refresh token sử dụng thuật toán không được hỗ trợ
	public static final String AUTH_REFRESH_TOKEN_BLACKLISTED        		= "SYS-AUTH-017"; // Refresh token đã bị thu hồi hoặc nằm trong blacklist
	public static final String AUTH_REFRESH_TOKEN_UNKNOWN_ERROR      	= "SYS-AUTH-018"; // Lỗi không xác định khi xử lý refresh token
	
	// ==== AUTHENTICATION / ACCOUNT ====
	public static final String AUTH_INVALID_CREDENTIALS             			= "SYS-AUTH-019"; // Sai thông tin đăng nhập (email/mật khẩu không đúng)
	public static final String AUTH_ACCOUNT_NOT_FOUND              			= "SYS-AUTH-020"; // Tài khoản không tồn tại
	public static final String AUTH_ACCOUNT_LOCKED                 				= "SYS-AUTH-021"; // Tài khoản bị khóa
	public static final String AUTH_ACCOUNT_INACTIVE               				= "SYS-AUTH-022"; // Tài khoản chưa được kích hoạt
	public static final String AUTH_ACCOUNT_ALREADY_EXISTS         			= "SYS-AUTH-023"; // Tài khoản đã tồn tại
	public static final String AUTH_ACCESS_DENIED                  				= "SYS-AUTH-024"; // Không có quyền truy cập
	
	// ==== AUTHENTICATION / OTP ====
	public static final String AUTH_OTP_INVALID                    					= "SYS-AUTH-025"; // Mã OTP không hợp lệ
	public static final String AUTH_OTP_EXPIRED                    					= "SYS-AUTH-026"; // Mã OTP đã hết hạn
	public static final String AUTH_OTP_TOO_MANY_ATTEMPTS          			= "SYS-AUTH-027"; // Nhập sai OTP quá nhiều lần
	public static final String AUTH_OTP_NOT_FOUND                  				= "SYS-AUTH-028"; // Không tìm thấy OTP
	
	// ==== AUTHENTICATION / TWO FACTOR ====
	public static final String AUTH_2FA_REQUIRED                   				= "SYS-AUTH-029"; // Yêu cầu xác thực hai bước
	public static final String AUTH_2FA_FAILED                    					= "SYS-AUTH-030"; // Xác thực hai bước thất bại
	
	// ==== SYSTEM ====
	public static final String SYSTEM_UNKNOWN_ERROR 					= "SYS-SYSTEM-000"; // Lỗi không xác định trong hệ thống
	public static final String SYS_INTERNAL_SERVER_ERROR    				= "SYS-SYSTEM-001"; // Lỗi không xác định từ phía server
	public static final String SYS_SERVICE_UNAVAILABLE      					= "SYS-SYSTEM-002"; // Dịch vụ tạm thời không khả dụng
	public static final String SYS_TIMEOUT                  						= "SYS-SYSTEM-003"; // Request bị timeout
	public static final String SYS_DB_CONNECTION_FAILED     				= "SYS-SYSTEM-004"; // Không thể kết nối tới cơ sở dữ liệu
	public static final String SYS_CONFIGURATION_ERROR      				= "SYS-SYSTEM-005"; // Lỗi cấu hình hệ thống
	
	// ==== API / REQUEST ====
	public static final String API_NOT_FOUND                     					= "SYS-API-001";    // API không tồn tại
	public static final String API_METHOD_NOT_ALLOWED            				= "SYS-API-002";    // Phương thức HTTP không được hỗ trợ
	public static final String API_UNSUPPORTED_MEDIA_TYPE        			= "SYS-API-003";    // Loại dữ liệu không được hỗ trợ (Content-Type)
	public static final String API_NOT_ACCEPTABLE                					= "SYS-API-004";    // Không chấp nhận loại phản hồi (Accept Header)
	public static final String API_BAD_REQUEST                   					= "SYS-API-005";    // Request không hợp lệ
	
	// ==== VALIDATION ====
	public static final String SYS_VALIDATION_ERROR          					= "SYS-VALID-001"; // Dữ liệu đầu vào không hợp lệ
	public static final String SYS_MISSING_REQUIRED_FIELD    				= "SYS-VALID-002"; // Thiếu trường bắt buộc
	public static final String SYS_INVALID_FORMAT            					= "SYS-VALID-003"; // Định dạng không hợp lệ
	public static final String SYS_CONSTRAINT_VIOLATION      				= "SYS-VALID-004"; // Vi phạm ràng buộc dữ liệu
	
	// ==== FILE / MEDIA ====
	public static final String SYS_FILE_TOO_LARGE             					= "SYS-FILE-001"; // File vượt quá dung lượng cho phép
	public static final String SYS_FILE_UNSUPPORTED_TYPE      				= "SYS-FILE-002"; // File không đúng định dạng cho phép
	public static final String SYS_FILE_UPLOAD_FAILED         					= "SYS-FILE-003"; // Lỗi trong quá trình upload file
	public static final String SYS_FILE_NOT_FOUND            					= "SYS-FILE-004"; // Không tìm thấy file yêu cầu
	
	
}
