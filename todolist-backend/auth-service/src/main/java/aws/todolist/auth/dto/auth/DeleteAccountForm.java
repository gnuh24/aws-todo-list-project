package aws.todolist.auth.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteAccountForm {
	
	/* =====================================================
	 * Current Email (bắt buộc)
	 * ===================================================== */
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;
	
	/* =====================================================
	 * Password (bắt buộc)
	 * ===================================================== */
	@NotBlank(message = "Mật khẩu không được để trống")
	private String password;
	
	/* =====================================================
	 * OTP (Email)
	 * Dùng khi 2FA OFF
	 * ===================================================== */
	@Size(min = 6, max = 6, message = "Mã OTP phải gồm 6 chữ số")
	private String otp;
	
	/* =====================================================
	 * TOTP (Authenticator)
	 * Dùng khi 2FA ON
	 * ===================================================== */
	private Integer totp;
	
	/* =====================================================
	 * Confirm text
	 * ===================================================== */
	@NotBlank(message = "Vui lòng nhập 'delete' để xác nhận xóa tài khoản")
	private String confirmText;
}
