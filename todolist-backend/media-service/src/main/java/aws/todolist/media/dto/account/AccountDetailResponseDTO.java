package aws.todolist.media.dto.account;

import jakarta.persistence.Column;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailResponseDTO {
    private String id;
    private String email;
    private String avatar;
    private String displayName;
    private String role;
    private String status;
    private boolean receiveEmail;
	private boolean twoFactorEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
