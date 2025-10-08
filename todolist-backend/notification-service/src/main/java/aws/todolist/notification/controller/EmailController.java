package aws.todolist.notification.controller;

import aws.todolist.notification.api.ApiResponse;
import aws.todolist.notification.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "Authentication API", description = "Đăng nhập, đăng ký và quản lý xác thực người dùng")
public class EmailController {
	
	@Autowired
	private EmailService emailService;

	
//	@PostMapping("/send-reset-password-otp/{email}")
//	public ResponseEntity<ApiResponse<String>> sendOtpForResetPassword(@PathVariable String email) {
//		emailService.(email);
//		return ResponseEntity.ok(
//		    new ApiResponse<>(200, "Hệ thống đã gửi OTP sang email " + email + ". Bạn có 3 phút để kiểm tra nhé", null)
//		);
//	}
//
//
//	@PostMapping("/send-update-email-otp/{newEmail}")
//	public ResponseEntity<ApiResponse<String>> sendOtpForUpdateEmail(@PathVariable String newEmail) {
//		emailService.sendOtpUpdateEmail(newEmail);
//		return ResponseEntity.ok(
//		    new ApiResponse<>(200, "Hệ thống đã gửi OTP sang email " + newEmail + ". Bạn có 3 phút để kiểm tra nhé", null)
//		);
//	}
	
}
