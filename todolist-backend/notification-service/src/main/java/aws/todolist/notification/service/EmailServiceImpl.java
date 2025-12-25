package aws.todolist.notification.service;

import aws.todolist.notification.entity.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Autowired
	@Lazy
	private AccountService accountService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${domain.frontend}")
	private String domainFrontEnd;
	
	@Override
	public void sendRegistrationUserConfirm(String email, String otp) {
		
		
		String confirmationUrl = domainFrontEnd + "/auth/verify-account?otp=" + otp + "&email=" + email;
		
		String subject = "Xác Nhận Đăng Ký Tài khoản";
		String content = getEmailContentForRegistration(confirmationUrl);
		
		sendEmail(email, subject, content);
	}
	
	private String getEmailContentForRegistration(String confirmationUrl) {
		return "<!DOCTYPE html>" +
		    "<html>" +
		    "<head>" +
		    "<style>" +
		    "body {font-family: Arial, sans-serif;}" +
		    ".container {padding: 20px;}" +
		    ".header {background-color: #4CAF50; padding: 10px; text-align: center; color: white;}" +
		    ".content {margin: 20px; padding: 20px; border: 1px solid #ddd; border-radius: 5px;}" +
		    ".button {background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; border-radius: 5px;}" +
		    ".footer {margin-top: 20px; text-align: center; color: #888;}" +
		    ".highlight {color: white; font-weight: bold;}" +  // Thêm lớp .highlight
		    "</style>" +
		    "</head>" +
		    "<body>" +
		    "<div class=\"container\">" +
		    "<div class=\"header\">" +
		    "<h1>Xác Nhận Đăng Ký Account</h1>" +
		    "</div>" +
		    "<div class=\"content\">" +
		    "<p>Chào bạn,</p>" +
		    "<p>Bạn đã đăng ký thành công. Click vào link dưới đây để kích hoạt tài khoản:</p>" +
		    "<p><a href=\"" + confirmationUrl + "\" class=\"button\"><span class=\"highlight\">Kích hoạt tài khoản</span></a></p>" +  // Áp dụng lớp .highlight
		    "</div>" +
		    "<div class=\"footer\">" +
		    "<p>Cảm ơn bạn vì đã tin tưởng chúng tôi!</p>" +
		    "</div>" +
		    "</div>" +
		    "</body>" +
		    "</html>";
	}
	
	@Override
	public void sendResetPasswordUserConfirm(String email, String otp) {
		String subject = "Mã xác nhận đổi mật khẩu";
		
		String content = getEmailContentWithSixDigitTokenPassword(otp);
		
		sendEmail(email, subject, content);
	}
	
	@Override
	public void sendUpdateEmailOtp(String username, String otp) {
		String subject = "Mã xác nhận đổi email";
		
		String content = getEmailContentWithSixDigitTokenPassword(otp);
		
		sendEmail(username, subject, content);
	}

	@Override
	public void sendNotification(Notification notification) {
		String subject = "Thông báo từ hệ thống TODOIST";

		String content = getEmailContentForNotification(notification);

		sendEmail(notification.getReceiver().getEmail(),subject,content);
	}

//    @Override
//    public void sendUpdatePasswordUserConfirm(Account account, OTP otp) {
//        String subject = "Mã xác nhận đổi mật khẩu";
//        String content = getEmailContentWithSixDigitTokenPassword(otp.getCode());
//        sendEmail(account.getEmail(), subject, content);
//    }
//
//    @Override
//    public void sendUpdateEmailUserConfirm(String newEmail, OTP otp) {
//
//        String subject = "Mã xác nhận đổi email";
//
//        String content = getEmailContentWithSixDigitTokenPassword(otp.getCode());
//
//        sendEmail(newEmail, subject, content);
//
//    }
//

	
	private String getEmailContentWithSixDigitTokenPassword(String token) {
		return "<!DOCTYPE html>" +
		    "<html>" +
		    "<head>" +
		    "<style>" +
		    "body {font-family: Arial, sans-serif;}" +
		    ".container {padding: 20px;}" +
		    ".header {background-color: #4CAF50; padding: 10px; text-align: center; color: white;}" +
		    ".content {margin: 20px; padding: 20px; border: 1px solid #ddd; border-radius: 5px;}" +
		    ".button {background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; border-radius: 5px;}" +
		    ".footer {margin-top: 20px; text-align: center; color: #888;}" +
		    ".highlight {color: white; font-weight: bold;}" +
		    "</style>" +
		    "</head>" +
		    "<body>" +
		    "<div class=\"container\">" +
		    "<div class=\"header\">" +
		    "<h1>Mã xác thực thay đổi mật khẩu</h1>" +
		    "</div>" +
		    "<div class=\"content\">" +
		    "<p>Chào bạn,</p>" +
		    "<p>Mã xác thực đổi mật khẩu</p>" +
		    "<p class=\"button\"><span class=\"highlight\">" + token + "</span></p>" +  // Display the token
		    "</div>" +
		    "<div class=\"footer\">" +
		    "<p>Cảm ơn bạn vì đã tin tưởng chúng tôi!</p>" +
		    "</div>" +
		    "</div>" +
		    "</body>" +
		    "</html>";
	}

	private String getEmailContentForNotification(Notification notification){
		return "<!DOCTYPE html>" +
				"<html>" +
				"<head>" +
				"<style>" +
				"body {font-family: Arial, sans-serif; background: #f5f5f5;}" +
				".container {max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px;}" +
				".header {background-color: #2563eb; padding: 15px; text-align: center; color: white; border-radius: 6px;}" +
				".content {margin-top: 20px; padding: 20px; border: 1px solid #ddd; border-radius: 6px;}" +
				".button {background-color: #2563eb; color: white; padding: 12px 20px; text-align: center; text-decoration: none; display: inline-block; border-radius: 6px; font-weight: bold;}" +
				".footer {margin-top: 18px; text-align: center; color: #888; font-size: 13px;}" +
				"</style>" +
				"</head>" +
				"<body>" +
				"<div class=\"container\">" +
				"<div class=\"header\">" +
				"<h2>" + notification.getTitle() + "</h2>" +
				"</div>" +

				"<div class=\"content\">" +
				"<p>Xin chào <b>" + notification.getReceiver().getDisplayName() + "</b>,</p>" +
				"<p>" + notification.getContent() + "</p>" +
				"<p style='margin-top: 25px; text-align:center;'>" +
				"</p>" +
				"</div>" +

				"<div class=\"footer\">" +
				"<p>Đây là email tự động. Vui lòng không phản hồi.</p>" +
				"<p>Cảm ơn bạn đã sử dụng hệ thống của chúng tôi!</p>" +
				"</div>" +
				"</div>" +
				"</body>" +
				"</html>";
	}
	

	
	private void sendEmail(final String recipientEmail, final String subject, final String content) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(recipientEmail);
			helper.setSubject(subject);
			helper.setText(content, true); // true indicates HTML
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
}


