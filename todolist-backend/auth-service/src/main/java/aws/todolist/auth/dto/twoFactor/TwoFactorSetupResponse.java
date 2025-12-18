package aws.todolist.auth.dto.twoFactor;

import lombok.Data;

@Data
public class TwoFactorSetupResponse {
    private String qrUrl;
    private String secret;
}