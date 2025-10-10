package aws.todolist.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nội dung hiển thị cho người dùng
    @Column(nullable = false, length = 512)
    private String message;

    // Loại thông báo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    // Người nhận
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    // Người gửi (có thể null, ví dụ hệ thống tự tạo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Account sender;

    // Tham chiếu tới project hoặc task liên quan
    @Column(name = "reference_id")
    private String referenceId;

    // Thời gian tạo
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Đánh dấu đã đọc
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // ==== ENUM LOẠI THÔNG BÁO ====
    public enum NotificationType {

        PROJECT_MEMBER_JOINED,   // Khi có thành viên mới join project
        PROJECT_MEMBER_ADDED,    // Khi user được add vào project

        TASK_ASSIGNED,           // Khi task được giao cho mình
        TASK_COMMENTED,          // Khi có ai đó comment vào task mình liên quan
        TASK_UPDATED,            // Khi task được cập nhật
        TASK_COMPLETED,          // Khi task hoàn thành
        TASK_REOPENED,           // Khi task bị reopen

        TASK_DUE_SOON,           // Khi task sắp tới hạn
        TASK_OVERDUE,            // Khi task quá hạn

        PROJECT_UPDATED,         // Khi project được cập nhật
        PROJECT_DELETED,         // Khi project bị xóa

        MENTION_IN_COMMENT       // Khi mình bị tag/mention trong comment
    }
}
