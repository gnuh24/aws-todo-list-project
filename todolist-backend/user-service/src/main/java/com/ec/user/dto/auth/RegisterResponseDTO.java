package com.ec.user.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
		
		@Schema(description = "Mã ID của người dùng", example = "12345", required = true)
		private String id;
		
		@Schema(description = "Email của người dùng", example = "user@example.com", required = true)
		private String email;
}
