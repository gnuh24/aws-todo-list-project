package com.ec.catalog.service;


public interface EmailService {
	
	void sendRegistrationUserConfirm(String email, String otp);
	
	void sendResetPasswordUserConfirm(String email, String otp);
	
	void sendUpdateEmailOtp(String username, String otp);

//	void sendUpdatePasswordUserConfirm(Account account, OTP otp);
//
//	void sendUpdateEmailUserConfirm(String newEmail, OTP otp);
//
}
