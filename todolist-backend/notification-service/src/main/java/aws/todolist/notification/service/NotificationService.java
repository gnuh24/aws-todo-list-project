package aws.todolist.notification.service;

import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

	    /**
	     * Lấy danh sách thông báo đã phân trang của người dùng hiện tại.
	     */
	    Page<Notification> getNotifications(String receiverId, Boolean isRead, Pageable pageable); // Thêm filter

	Integer markNotificationsByIsRead(List<String> ids, boolean isRead);

	void create(NotificationMessage msg);
	int updateReadStatus(String notificationId, boolean isRead); // Đã bỏ receiverId
	Long getCountNotificationByIsRead(boolean isRead, String idReceiver);

}