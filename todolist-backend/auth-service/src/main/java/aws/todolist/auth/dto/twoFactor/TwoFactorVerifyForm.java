package aws.todolist.auth.dto.twoFactor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TwoFactorVerifyForm {
    @NotBlank(message = "OTP không được để trống")
    private String otp;
}