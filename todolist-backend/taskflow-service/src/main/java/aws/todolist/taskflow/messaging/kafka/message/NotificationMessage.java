package aws.todolist.taskflow.messaging.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private String receiverId;     // người nhận
    private String actorId;        // người thực hiện
    private String projectId;      // project liên quan
    private String taskId;         // task liên quan
    private NotificationType type; // loại thông báo
    private String title;          // tiêu đề
    private String content;        // nội dung
}