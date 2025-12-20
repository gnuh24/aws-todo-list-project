package aws.todolist.auth.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
	
	@Schema(description = "Mã ID của người dùng", example = "12345")
	private String id;
	
	@Schema(description = "Email của người dùng", example = "user@example.com")
	private String email;
	
	@Schema(description = "Display name của người dùng", example = "Ngô Tuấn Hưng")
	private String displayName;
	
	@Schema(description = "Ảnh đại diện của người dùng", example = "https://example.com/avatar.jpg")
	private String avatar;
	
	@Schema(description = "Vai trò của người dùng", example = "ADMIN")
	private String role;
	
	@Schema(description = "Mã token để xác thực", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String token;
	
	private boolean receiveEmail;
	
	private boolean twoFactorEnabled;
	
	@Schema(description = "Thời gian hết hạn của token", example = "2025-05-10T12:00:00")
	private String tokenExpirationTime;
	
	@Schema(description = "Mã refresh token để làm mới token", example = "dGhpc0lzYSByZWZyZXNoVG9rZW5z...")
	private String refreshToken;
	
	@Schema(description = "Thời gian hết hạn của refresh token", example = "2025-05-10T12:00:00")
	private String refreshTokenExpirationTime;
}
