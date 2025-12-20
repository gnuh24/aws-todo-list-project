package aws.todolist.auth.dto.twoFactor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFactorDisableForm {
	
	
	@NotNull(message = "OTP không được để trống")
	private int otp;
	
}
