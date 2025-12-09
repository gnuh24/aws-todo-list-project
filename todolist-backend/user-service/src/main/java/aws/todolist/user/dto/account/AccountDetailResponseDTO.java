package aws.todolist.user.dto.account;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
