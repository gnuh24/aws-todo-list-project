package aws.todolist.notification.service;


import aws.todolist.notification.entity.Notification;

public interface EmailService {
	
	void sendRegistrationUserConfirm(String email, String otp);
	
	void sendResetPasswordUserConfirm(String email, String otp);
	
	void sendUpdateEmailOtp(String username, String otp);

	void sendNotification(Notification notification);

//	void sendUpdatePasswordUserConfirm(Account account, OTP otp);
//
//	void sendUpdateEmailUserConfirm(String newEmail, OTP otp);
//
}
