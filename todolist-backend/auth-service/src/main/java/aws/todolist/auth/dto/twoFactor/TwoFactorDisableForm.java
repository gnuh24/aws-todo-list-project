package aws.todolist.auth.dto.twoFactor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFactorDisableForm {
	
	private int otp;
	
	private String recoveryKey;
	
}
