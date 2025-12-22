package aws.todolist.notification.dto.eventNotification;

import aws.todolist.notification.dto.notification.NotificationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEventPayload {

    /**
     * Email người nhận (dùng để gửi mail / push)
     */
    private String email;

    /**
     * Dữ liệu notification hiển thị
     */
    private NotificationResponse notification;
}
