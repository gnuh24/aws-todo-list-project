package aws.todolist.notification.service;

import aws.todolist.notification.dto.notification.NotificationCreateForm;
import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INotificationService {

	    /**
	     * Lấy danh sách thông báo đã phân trang của người dùng hiện tại.
	     */
	    Page<Notification> getNotifications(String receiverId, Boolean isRead, Pageable pageable); // Thêm filter
//	int createNotification(NotificationCreateForm form);
	int updateReadStatus(String notificationId, boolean isRead); // Đã bỏ receiverId

}