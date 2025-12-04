package aws.todolist.user.exceptions.errorCode;

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
	
	// ==== ACCOUNT PROFILE ====
	public static final String ACCOUNT_PROFILE_NOT_FOUND                  = "SYS-ACC-001"; // Không tìm thấy hồ sơ người dùng
	public static final String ACCOUNT_PROFILE_UPDATE_FAILED             = "SYS-ACC-002"; // Cập nhật hồ sơ thất bại
	public static final String ACCOUNT_ROLE_INVALID                      = "SYS-ACC-003"; // Quyền người dùng không hợp lệ
	public static final String ACCOUNT_ROLE_NOT_FOUND                    = "SYS-ACC-004"; // Không tìm thấy role
	public static final String ACCOUNT_PERMISSION_DENIED                 = "SYS-ACC-005"; // Người dùng không đủ quyền
	
	// ==== PERMISSION ====
	public static final String PERMISSION_INVALID                        = "SYS-PERM-001"; // Permission không hợp lệ
	public static final String PERMISSION_NOT_FOUND                      = "SYS-PERM-002"; // Không tìm thấy permission
	public static final String PERMISSION_ACCESS_DENIED                  = "SYS-PERM-003"; // Không đủ quyền truy cập
	public static final String PERMISSION_ASSIGN_FAILED                  = "SYS-PERM-004"; // Gán quyền thất bại
	
	// ==== DATABASE ====
	public static final String DB_QUERY_FAILED                           = "SYS-DB-001"; // Lỗi khi truy vấn cơ sở dữ liệu
	public static final String DB_DUPLICATE_KEY                          = "SYS-DB-002"; // Trùng khóa (unique key)
	public static final String DB_DATA_INTEGRITY_ERROR                   = "SYS-DB-003"; // Vi phạm ràng buộc dữ liệu
	public static final String DB_TRANSACTION_FAILED                     = "SYS-DB-004"; // Transaction thất bại
	public static final String DB_DATA_NOT_FOUND                         = "SYS-DB-005"; // Không tìm thấy dữ liệu
	public static final String DB_SEQUENCE_GENERATION_FAILED             = "SYS-DB-006"; // Lỗi sinh mã ID
	
	// ==== CACHE ====
	public static final String CACHE_NOT_FOUND                           = "SYS-CACHE-001"; // Không tìm thấy cache
	public static final String CACHE_READ_FAILED                         = "SYS-CACHE-002"; // Lỗi đọc cache
	public static final String CACHE_WRITE_FAILED                        = "SYS-CACHE-003"; // Lỗi ghi cache
	public static final String CACHE_CONNECTION_FAILED                   = "SYS-CACHE-004"; // Không thể kết nối cache (Redis,..)
	
	// ==== RATE LIMITING ====
	public static final String RATE_LIMIT_EXCEEDED                       = "SYS-RATE-001"; // Quá giới hạn request
	public static final String RATE_LIMIT_BLOCKED                        = "SYS-RATE-002"; // Bị block do spam
	
	// ==== EMAIL ====
	public static final String EMAIL_SEND_FAILED                         = "SYS-EMAIL-001"; // Gửi email thất bại
	public static final String EMAIL_TEMPLATE_NOT_FOUND                  = "SYS-EMAIL-002"; // Không tìm thấy template email
	public static final String EMAIL_INVALID_ADDRESS                     = "SYS-EMAIL-003"; // Email không hợp lệ
	
	// ==== SMS ====
	public static final String SMS_SEND_FAILED                           = "SYS-SMS-001"; // Gửi SMS thất bại
	public static final String SMS_INVALID_NUMBER                        = "SYS-SMS-002"; // Số điện thoại không hợp lệ
	
	// ==== THIRD PARTY API ====
	public static final String THIRD_PARTY_REQUEST_FAILED                = "SYS-3RD-001"; // Request tới service ngoài thất bại
	public static final String THIRD_PARTY_TIMEOUT                       = "SYS-3RD-002"; // Timeout từ service ngoài
	public static final String THIRD_PARTY_INVALID_RESPONSE              = "SYS-3RD-003"; // Phản hồi không hợp lệ từ service ngoài
	public static final String THIRD_PARTY_AUTH_FAILED                   = "SYS-3RD-004"; // Xác thực với service ngoài thất bại
	
	// ==== BUSINESS LOGIC ====
	public static final String BIZ_RULE_VIOLATION                        = "SYS-BIZ-001"; // Vi phạm rule nghiệp vụ
	public static final String BIZ_OPERATION_NOT_ALLOWED                 = "SYS-BIZ-002"; // Hành động không được phép
	public static final String BIZ_CONFLICT                              = "SYS-BIZ-003"; // Dữ liệu xung đột
	public static final String BIZ_RESOURCE_LIMIT_EXCEEDED               = "SYS-BIZ-004"; // Vượt quá giới hạn tài nguyên
	
	// ==== PAYMENT ====
	public static final String PAYMENT_FAILED                            = "SYS-PAY-001"; // Thanh toán thất bại
	public static final String PAYMENT_INVALID_METHOD                    = "SYS-PAY-002"; // Phương thức thanh toán không hợp lệ
	public static final String PAYMENT_GATEWAY_ERROR                     = "SYS-PAY-003"; // Lỗi từ cổng thanh toán
	public static final String PAYMENT_INSUFFICIENT_FUNDS                = "SYS-PAY-004"; // Không đủ số dư
	
	// ==== QUEUE / MESSAGE BROKER ====
	public static final String QUEUE_SEND_FAILED                         = "SYS-QUEUE-001"; // Gửi message thất bại
	public static final String QUEUE_RECEIVE_FAILED                      = "SYS-QUEUE-002"; // Nhận message thất bại
	public static final String QUEUE_CONNECTION_FAILED                   = "SYS-QUEUE-003"; // Không kết nối được queue
	
	// ==== WEBSOCKET ====
	public static final String WS_CONNECTION_FAILED                      = "SYS-WS-001"; // Kết nối WebSocket lỗi
	public static final String WS_MESSAGE_SEND_FAILED                    = "SYS-WS-002"; // Gửi message lỗi
	public static final String WS_SESSION_NOT_FOUND                      = "SYS-WS-003"; // Không tìm thấy session WebSocket
	
	// ==== SCHEDULED TASK ====
	public static final String SCHEDULE_EXECUTION_FAILED                 = "SYS-SCH-001"; // Chạy scheduler thất bại
	public static final String SCHEDULE_INVALID_CRON                     = "SYS-SCH-002"; // Cron không hợp lệ
	public static final String SCHEDULE_TASK_NOT_FOUND                   = "SYS-SCH-003"; // Không tìm thấy task
	
	// ==== DATA INTEGRITY ====
	public static final String DATA_CORRUPTED                            = "SYS-DATA-001"; // Dữ liệu bị corrupt
	public static final String DATA_MIGRATION_FAILED                     = "SYS-DATA-002"; // Migration thất bại
	public static final String DATA_VERSION_CONFLICT                     = "SYS-DATA-003"; // Versioning conflict (optimistic lock)
	
	
}
