package aws.todolist.auth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteAccountForm {
	
	/* =====================================================
	 * Password (bắt buộc)
	 * ===================================================== */
	@NotBlank(message = "Mật khẩu không được để trống")
	private String password;
	
//	/* =====================================================
//	 * TOTP (Google Authenticator) – optional
//	 * Dùng khi 2FA ON (validate bằng business logic)
//	 * ===================================================== */
//	@Size(min = 6, max = 6, message = "Mã TOTP phải gồm 6 chữ số")
//	private String totp;
	
	/* =====================================================
	 * OTP (Email) – optional
	 * Dùng khi 2FA OFF (validate bằng business logic)
	 * ===================================================== */
	@Size(min = 6, max = 6, message = "Mã OTP phải gồm 6 chữ số")
	private String otp;
	
	/* =====================================================
	 * Confirm text
	 * ===================================================== */
	@NotBlank(message = "Vui lòng nhập 'delete' để xác nhận xóa tài khoản")
	private String confirmText;
}
