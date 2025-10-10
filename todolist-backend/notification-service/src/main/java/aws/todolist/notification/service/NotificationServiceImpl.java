package aws.todolist.notification.service;


import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
	
	@Override
	public Page<Notification> getNotifications(String receiverId, Boolean isRead, Pageable pageable) {
		// Gọi phương thức mới của Repository
		return notificationRepository.findNotificationsByFilter(receiverId, isRead, pageable);
	}
	
	@Override
	@Transactional
	public int updateReadStatus(String notificationId, boolean isRead) { // Đã bỏ receiverId
		// Chỉ truyền notificationId và isRead vào Repository
		return notificationRepository.updateReadStatus(notificationId, isRead);
	}


}