package aws.todolist.notification.dto.notification;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateReadStatusRequest {
    
    // Trạng thái mới của is_read (true = đã đọc, false = chưa đọc)
    @NotNull(message = "isRead status must be provided.")
    private Boolean isRead; 
}