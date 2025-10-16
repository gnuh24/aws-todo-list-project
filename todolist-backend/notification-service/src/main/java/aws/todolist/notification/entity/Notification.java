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
	@Column(length = 36) // CHAR(36) in SQL
	private String id;
	
	// Người nhận thông báo (receiver_id) - Gán thẳng Account theo yêu cầu
	// FOREIGN KEY (`receiver_id`) REFERENCES `account`(`id`)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private Account receiver;
	
	// Người thực hiện hành động (actor_id) - Gán thẳng Account theo yêu cầu
	// FOREIGN KEY (`actor_id`) REFERENCES `account`(`id`)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actor_id")
	private Account actor;
	
	// Nếu thông báo liên quan tới project (Giữ lại ID vì thuộc service khác)
	@Column(name = "project_id", length = 36)
	private String projectId;
	
	// Nếu liên quan tới task (Giữ lại ID vì thuộc service khác)
	@Column(name = "task_id", length = 36)
	private String taskId;
	
	// Loại thông báo
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 50)
	private NotificationType type;
	
	// Tiêu đề thông báo
	@Column(name = "title", nullable = false, length = 255)
	private String title;
	
	// Nội dung thông báo
	@Lob // Maps to TEXT in SQL
	@Column(name = "content", nullable = false)
	private String content;
	
	// Đã đọc chưa
	@Column(name = "is_read", nullable = false)
	private boolean isRead = false;
	
	// Thời gian tạo (Sử dụng @CreationTimestamp nếu cần tự động)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	
	// Thời gian đọc
	@Column(name = "read_at")
	private LocalDateTime readAt;
	
	// Thời gian xóa (Soft Delete)
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
	
	// Đã xóa (Soft Delete flag)
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;
	
	// ==== ENUM LOẠI THÔNG BÁO ====
	// Có thể đặt trong file riêng hoặc giữ lại như sau:
	public enum NotificationType {
		PROJECT_MEMBER_ADDED,         // Khi được thêm vào project
		PROJECT_MEMBER_ROLE_UPDATED,  // Khi vai trò trong project thay đổi
		TASK_ASSIGNED,                // Khi được giao task
		TASK_COMMENTED,               // Khi có người comment vào task liên quan
		TASK_UPDATED,                 // Khi task được cập nhật
		TASK_COMPLETED,               // Khi task hoàn thành
		TASK_REOPENED,                // Khi task bị mở lại
		TASK_DUE_SOON,                // Khi task sắp đến hạn
		TASK_OVERDUE,                 // Khi task bị trễ hạn
		PROJECT_DELETED               // Khi project bị xóa
	}
}