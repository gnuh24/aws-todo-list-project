package aws.todolist.auth.controller;

import aws.todolist.auth.api.ApiResponse;
import aws.todolist.auth.dto.account.AccountRedisDTO;
import aws.todolist.auth.dto.auth.*;
import aws.todolist.auth.dto.twoFactor.TwoFactorSetupResponse;
import aws.todolist.auth.dto.twoFactor.TwoFactorVerifyForm;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorException;
import aws.todolist.auth.security.JwtTokenProvider;
import aws.todolist.auth.service.AccountService;
import aws.todolist.auth.service.AuthService;
import aws.todolist.auth.service.TwoFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "Authentication API", description = "Đăng nhập, đăng ký và quản lý xác thực người dùng")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private TwoFactorService twoFactorService;
	
	/**
	 * 📌 Kiểm tra email đã tồn tại chưa
	 *
	 * @param email cần kiểm tra
	 * @return Trạng thái tồn tại của email
	 */
	@Operation(summary = "Kiểm tra email tồn tại", description = "Kiểm tra xem email đã được đăng ký trong hệ thống chưa.")
	@GetMapping("/check-email")
	public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
	    @Parameter(description = "Email cần kiểm tra", example = "user@example.com")
	    @RequestParam String email) {
		
		boolean exists = authService.isEmailExists(email);
		ApiResponse<Boolean> response = new ApiResponse<>(200, "Email existence check completed successfully", exists);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/2fa/setup")
	public ResponseEntity<ApiResponse<TwoFactorSetupResponse>> setup2FA(
		@RequestHeader("X-User-Email") String email
	) {
	
//		if (account.isTwoFactorEnabled()) {
//			throw new TwoFactorException("Tài khoản đã bật 2FA");
//		}
		
		TwoFactorSetupResponse response = twoFactorService.setup2FA(email);
		
		return ResponseEntity.ok(
			new ApiResponse<>(200, "Tạo thông tin 2FA thành công", response)
		);
	}
	
	@PostMapping("/2fa/verify")
	public ResponseEntity<ApiResponse<Void>> verify2FA(
		@RequestHeader("X-User-Id") String accountId,
		@Valid @RequestBody TwoFactorVerifyForm form
	) {
		twoFactorService.verify2FA(accountId, form.getOtp());
		
		return ResponseEntity.ok(
			new ApiResponse<>(200, "Xác thực 2FA thành công", null)
		);
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
	    @RequestBody @Valid LoginRequestForm loginInputForm) {
		
		AuthResponseDTO loginInfo = authService.login(loginInputForm);
		
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

//		// ✅ Gắn cookie refresh_token
//		addRefreshTokenCookie(response, loginInfo.getRefreshToken());
		
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
		
		// Tạo tài khoản
		AccountRedisDTO account = authService.register(form);
		
		// Chuyển đổi tài khoản sang DTO
		RegisterResponseDTO authResponseDTO = new RegisterResponseDTO();
		authResponseDTO.setId(account.getId());
		authResponseDTO.setEmail(account.getEmail());
		
		// Trả về phản hồi
		return ResponseEntity.ok(new ApiResponse<>(201, "Account created successfully. Please activate your account on your email: " + account.getEmail(), authResponseDTO));
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
		responseDTO.setEmail(account.getUsername());
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Verify successfully", responseDTO));
	}
	
	@PostMapping("/send-reset-password-otp/{email}")
	public ResponseEntity<ApiResponse<String>> sendOtpForResetPassword(@PathVariable String email) {
		authService.sendOtpResetPassword(email);
		return ResponseEntity.ok(
		    new ApiResponse<>(200, "Hệ thống đã gửi OTP sang email " + email + ". Bạn có 3 phút để kiểm tra nhé", null)
		);
	}
	
	
	@PostMapping("/send-update-email-otp/{newEmail}")
	public ResponseEntity<ApiResponse<String>> sendOtpForUpdateEmail(@PathVariable String newEmail) {
		authService.sendOtpUpdateEmail(newEmail);
		return ResponseEntity.ok(
		    new ApiResponse<>(200, "Hệ thống đã gửi OTP sang email " + newEmail + ". Bạn có 3 phút để kiểm tra nhé", null)
		);
	}
	
	@PostMapping("/send-delete-account-otp")
	public ResponseEntity<ApiResponse<String>> sendOtpForDeleteAccount(
		@RequestHeader("X-User-Email") String email
	) {
		
		authService.sendOtpDeleteAccount(email);
		
		return ResponseEntity.ok(
			new ApiResponse<>(
				200,
				"Hệ thống đã gửi OTP xác nhận xóa tài khoản vào email của bạn. Mã có hiệu lực trong 3 phút.",
				null
			)
		);
	}
	
	
	
	@PatchMapping("/update-email")
	public ResponseEntity<ApiResponse<String>> updateEmail(
	    @RequestBody @Valid UpdateEmailForm form,
	    @RequestHeader("X-User-Id") String accountId
	) {
		
		authService.updateEmail(accountId, form);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Email updated successfully", null));
	}
	
	@PatchMapping("/reset-password/{username}")
	public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable String username,
								 @RequestBody @Valid ResetPasswordForm form) {
		
		authService.resetPassword(username, form);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Password updated successfully", null));
	}
	
	@PatchMapping("/update-password")
	public ResponseEntity<ApiResponse<String>> updatePassword(
	    @RequestBody @Valid UpdatePasswordForm form,
	    @RequestHeader("X-User-Id") String accountId
	) {
		
		authService.updatePassword(accountId, form);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Password updated successfully", null));
	}
	
	@Operation(summary = "Làm mới token", description = "Làm mới token truy cập bằng cách sử dụng refresh token.")
	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
	    @CookieValue(value = "refresh_token", required = false) String refreshToken) {
		
		// Gọi service để xử lý refresh token và nhận AuthResponseDTO
		AuthResponseDTO authResponse = authService.refreshToken(refreshToken);
		
		return ResponseEntity.ok(new ApiResponse<>(
		    200,
		    "Refresh token thành công",
		    authResponse
		));
	}
	
	
	@DeleteMapping("/delete-account")
	public ResponseEntity<ApiResponse<String>> deleteAccount(
		@RequestBody @Valid DeleteAccountForm form,
		@RequestHeader("X-User-Id") String accountId
	) {
		authService.deleteAccount(accountId, form);
		
		return ResponseEntity.ok(
			new ApiResponse<>(
				200,
				"Account deleted successfully",
				null
			)
		);
	}
	
	
}
