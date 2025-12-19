package aws.todolist.auth.dto.twoFactor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFactorVerifyForm {
    @NotNull(message = "OTP không được để trống")
    private int otp;
}