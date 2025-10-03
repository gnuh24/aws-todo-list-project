package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.account.AccountRedisDTO;
import com.ec.catalog.dto.auth.*;

import com.ec.catalog.entity.Account;
import com.ec.catalog.service.AccountService;
import com.ec.catalog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API", description = "Đăng nhập, đăng ký và quản lý xác thực người dùng")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * 📌 Kiểm tra username đã tồn tại chưa
	 *
	 * @param username cần kiểm tra
	 * @return Trạng thái tồn tại của username
	 */
	@Operation(summary = "Kiểm tra email tồn tại", description = "Kiểm tra xem email đã được đăng ký trong hệ thống chưa.")
	@GetMapping("/check-username")
	public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(@Parameter(description = "Email cần kiểm tra", example = "user@example.com") @RequestParam String username) {
		
		boolean exists = authService.isUsernameExists(username);
		ApiResponse<Boolean> response = new ApiResponse<>(200, "Email existence check completed successfully", exists);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 📌 Đăng nhập người dùng
	 *
	 * @param loginInputForm Thông tin đăng nhập
	 * @return Thông tin đăng nhập người dùng
	 */
	@Operation(summary = "Đăng nhập người dùng", description = "Đăng nhập người dùng vào hệ thống.")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> loginUser(
	    	@RequestBody @Valid LoginRequestForm loginInputForm,
	    	HttpServletResponse response) {
		
		AuthResponseDTO loginInfo = authService.login(loginInputForm);
		
		// ✅ Gắn cookie refresh_token
		addRefreshTokenCookie(response, loginInfo.getRefreshToken());
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", loginInfo));
	}
	
	/**
	 * 📌 Đăng nhập nhân viên
	 *
	 * @param loginInputForm Thông tin đăng nhập nhân viên
	 * @return Thông tin đăng nhập nhân viên
	 */
	@Operation(summary = "Đăng nhập nhân viên", description = "Đăng nhập nhân viên vào hệ thống.")
	@PostMapping("/staff-login")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> loginStaff(
	    	@RequestBody @Valid LoginRequestForm loginInputForm,
	    	HttpServletResponse response) {
		
		AuthResponseDTO loginInfo = authService.staffLogin(loginInputForm);
		
		// ✅ Gắn cookie refresh_token
		addRefreshTokenCookie(response, loginInfo.getRefreshToken());
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", loginInfo));
	}
	
	/**
	 * 📌 Đăng ký tài khoản người dùng
	 *
	 * @param form Thông tin đăng ký tài khoản
	 * @return Thông tin tài khoản mới được tạo
	 */
	@Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản mới cho người dùng.")
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<RegisterResponseDTO>> createAccount(@RequestBody @Valid UserRegistrationForm form) {
		System.err.println("Call API register");
		
		// Tạo tài khoản
		AccountRedisDTO account = authService.register(form);
		
		// Chuyển đổi tài khoản sang DTO
		RegisterResponseDTO authResponseDTO = new RegisterResponseDTO();
		authResponseDTO.setId(account.getId());
		authResponseDTO.setEmail(account.getUsername());
		
		// Trả về phản hồi
		return ResponseEntity.ok(new ApiResponse<>(201, "Account created successfully. Please activate your account on your email: " + account.getUsername(), authResponseDTO));
	}
	
	/**
	 * 📌 Đăng nhập nhân viên
	 *
	 * @param otp Thông tin đăng nhập nhân viên
	 * @return Thông tin đăng nhập nhân viên
	 */
	@Operation(summary = "Đăng nhập nhân viên", description = "Đăng nhập nhân viên vào hệ thống.")
	@PostMapping("/active-account")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> activeAccount(@RequestParam String otp) {
		
		Account account = authService.activeAccount(otp);
		AuthResponseDTO responseDTO = new AuthResponseDTO();
		responseDTO.setId(account.getId());
		responseDTO.setUsername(account.getUsername());
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Verify successfully", responseDTO));
	}
	
	@PostMapping("/send-reset-password-otp/{username}")
	public ResponseEntity<ApiResponse<String>> sendOtpForResetPassword(@PathVariable String username) {
		authService.sendOtpResetPassword(username);
		return ResponseEntity.ok(
		    new ApiResponse<>(
			200, // HTTP status code
			"Hệ thống đã gửi OTP sang email " + username + " .Bạn có 3 phút để kiểm tra nhé", // Success message
			null
		    )
		);
	}
	
	@PostMapping("/send-update-email-otp/{username}")
	public ResponseEntity<ApiResponse<String>> sendOtpForUpdateEmail(@PathVariable String username) {
		authService.sendOtpUpdateEmail(username);
		return ResponseEntity.ok(
		    new ApiResponse<>(
			200, // HTTP status code
			"Hệ thống đã gửi OTP sang email " + username + " .Bạn có 3 phút để kiểm tra nhé", // Success message
			null
		    )
		);
	}
	
	@PatchMapping("/update-email")
	public ResponseEntity<ApiResponse<String>> updateEmail(@RequestBody @Valid UpdateEmailForm form) {
		
		authService.updateEmail(form);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Password updated successfully", null));
	}
	
	@PatchMapping("/reset-password/{username}")
	public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable String username,
								 @RequestBody @Valid ResetPasswordForm form) {
		
		authService.resetPassword(username, form);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Password updated successfully", null));
	}
	
	@PatchMapping("/update-password")
	public ResponseEntity<ApiResponse<String>> updatePassword(@RequestBody @Valid UpdatePasswordForm form) {
		
		authService.updatePassword(form);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Password updated successfully", null));
	}
	
	@Operation(summary = "Làm mới token", description = "Làm mới token truy cập bằng cách sử dụng refresh token.")
	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(HttpServletRequest request) {
		
		// Gọi service để xử lý refresh token và nhận AuthResponseDTO
		AuthResponseDTO authResponse = authService.refreshToken(request);
		
		return ResponseEntity.ok(new ApiResponse<>(
		    200,
		    "Refresh token thành công",
		    authResponse
		));
	}
	
	private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie cookie = new Cookie("refresh_token", refreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		
		cookie.setPath("/api/user/auth/");

//		cookie.setPath("/api/user/auth/refresh-token");
//		System.err.println(ApiPath.REFRESH_TOKEN);
		
		cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
		
		response.addCookie(cookie);
	}
	
}
