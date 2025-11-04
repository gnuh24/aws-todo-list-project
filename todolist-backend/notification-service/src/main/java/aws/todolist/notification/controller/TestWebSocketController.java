package aws.todolist.notification.controller;

import aws.todolist.notification.entity.Account;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/notifications")
public class TestWebSocketController {

    @Autowired
    private WebSocketService notificationService;

    Notification sampleNotification = Notification.builder()
            .id("550e8400-e29b-41d4-a716-446655440000") // UUID mẫu
            .receiver(null) // đối tượng receiver mẫu
            .actor(null)       // actor mẫu, hoặc null nếu không có
            .projectId("proj-12345")                           // projectId mẫu
            .taskId("task-98765")                              // taskId mẫu
            .type(Notification.NotificationType.TASK_ASSIGNED)                             // loại notification
            .title("Bạn có task mới!")                         // title hiển thị
            .content("Bạn được giao nhiệm vụ hoàn thành báo cáo tuần này.") // nội dung chi tiết
            .isRead(false)                                     // chưa đọc
            .createdAt(LocalDateTime.now())                    // thời gian tạo
            .isDeleted(false)                                  // chưa xóa
            .build();

    @PostMapping("/toUser/{username}")
    public ResponseEntity<String> notifyUser(
            @PathVariable String username) {
        notificationService.sendToUserNew(username, sampleNotification);
        return ResponseEntity.ok("Sent to " + username);
    }

    @PostMapping("/toAll")
    public ResponseEntity<String> notifyAllUser() {
        notificationService.sendGlobal(sampleNotification);
        return ResponseEntity.ok("Broadcast sent");
    }

}
