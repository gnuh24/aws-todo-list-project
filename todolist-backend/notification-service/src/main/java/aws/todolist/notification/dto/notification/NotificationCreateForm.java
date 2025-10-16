package aws.todolist.notification.dto.notification;

import aws.todolist.notification.entity.Notification.NotificationType;
import lombok.Data;

/**
 * Form đầu vào cho việc tạo thông báo.
 * Service sẽ tự phân tích Business Logic dựa trên Type và Task/Project IDs để tìm người nhận.
 */
@Data
public class NotificationCreateForm {
	
	// Bắt buộc: Người thực hiện hành động (Admin, User, hoặc NULL nếu là hệ thống/hệ thống tự động gửi)
	private String actorId;
	
	// Tùy chọn: ID đối tượng liên quan
	private String projectId;
	private String taskId;
	
	// Bắt buộc: Loại thông báo
	private NotificationType type;
	
	// Bắt buộc: Tiêu đề và nội dung thông báo
	private String title;
	private String content;
	
	// Đã loại bỏ: private String relatedEntityId;
}