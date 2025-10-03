package com.ec.order.integration.redis;

public class RedisConstants {
	
	// 1. Email tồn tại
	public static final String USERNAME_EXIST = "username_exist";
	
	// 2. OTP xác thực tài khoản
	public static final String OTP_VERIFY_ACCOUNT = "otp:verify_account";
	
	// 3. OTP quên mật khẩu
	public static final String OTP_FORGOT_PASSWORD = "otp:forgot_password";
	
	// 4. OTP đổi email
	public static final String OTP_CHANGE_EMAIL = "otp:change_email";
	
	// 5. Đơn hàng ảo
	public static final String TEMP_ORDER = "temp:order";
	
	// 6. Ban list access token
	public static final String BANLIST_ACCESS_TOKEN = "banlist:access-token";
	
	// 7. Ban list refresh token
	public static final String BANLIST_REFRESH_TOKEN = "banlist:refresh-token";
	
	// 8. Cart Item
	public static final String CART_ITEM = "cart";
	
	
}
