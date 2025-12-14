package aws.todolist.auth.integration.redis;

public class RedisConstants {
	
	// 1. Email tồn tại
	public static final String EMAIL_EXIST = "email_exist";
	
	// 2. OTP xác thực tài khoản
	public static final String OTP_VERIFY_ACCOUNT = "otp:verify_account";
	
	// 3. OTP quên mật khẩu
	public static final String OTP_FORGOT_PASSWORD = "otp:forgot_password";
	
	// 4. OTP đổi email
	public static final String OTP_CHANGE_EMAIL = "otp:change_email";
	
	// 5. Đơn hàng ảo
	public static final String TEMP_ORDER = "temp:order";
	
	// 6. Ban list access token
	public static final String BANLIST_ACCOUNT_ID = "banlist:accountId";
	
}
