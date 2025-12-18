package aws.todolist.auth.service;

import aws.todolist.auth.dto.twoFactor.TwoFactorSetupResponse;
import aws.todolist.auth.entity.Account;

public interface TwoFactorService {
	
	/**
	 * Tạo secret tạm thời + QR URL
	 */
	TwoFactorSetupResponse setup2FA(String email);
//	/**
//	 * Verify OTP và bật 2FA cho account
//	 */
//	void verify2FA(Account account, int otp);
//
//	/**
//	 * Verify OTP khi login
//	 */
//	boolean verifyLoginOTP(Account account, int otp);
}
