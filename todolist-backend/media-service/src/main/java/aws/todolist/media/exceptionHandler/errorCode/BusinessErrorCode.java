package aws.todolist.media.exceptionHandler.errorCode;

public class BusinessErrorCode {
	
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
	
	
	// ==== QUEUE / MESSAGE BROKER ====
	public static final String QUEUE_SEND_FAILED                         = "SYS-QUEUE-001"; // Gửi message thất bại
	public static final String QUEUE_RECEIVE_FAILED                      = "SYS-QUEUE-002"; // Nhận message thất bại
	public static final String QUEUE_CONNECTION_FAILED                   = "SYS-QUEUE-003"; // Không kết nối được queue
	
	// ==== WEBSOCKET ====
	public static final String WS_CONNECTION_FAILED                      = "SYS-WS-001"; // Kết nối WebSocket lỗi
	public static final String WS_MESSAGE_SEND_FAILED                    = "SYS-WS-002"; // Gửi message lỗi
	public static final String WS_SESSION_NOT_FOUND                      = "SYS-WS-003"; // Không tìm thấy session WebSocket
	
	
}
