package aws.todolist.notification.service;


import aws.todolist.notification.dto.notification.NotificationCreateForm;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.entity.Notification.NotificationType; // Cần thêm import này
import aws.todolist.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections; // Cần thêm import này
import java.util.List;
import java.util.UUID; // Cần thêm import này
import java.util.stream.Collectors; // Cần thêm import này

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
	
	private final NotificationRepository notificationRepository;
	
	// GIẢ ĐỊNH: Các service cần thiết để truy vấn dữ liệu nghiệp vụ
	// @Autowired private TaskService taskService;
	// @Autowired private ProjectService projectService;
	
	@Override
	public Page<Notification> getNotifications(String receiverId, Boolean isRead, Pageable pageable) {
		// Gọi phương thức mới của Repository
		return notificationRepository.findNotificationsByFilter(receiverId, isRead, pageable);
	}
	
	@Override
	@Transactional
	public int updateReadStatus(String notificationId, boolean isRead) {
		// Chỉ truyền notificationId và isRead vào Repository
		return notificationRepository.updateReadStatus(notificationId, isRead);
	}
	
	
//	@Override
//	@Transactional
//	public int createNotification(NotificationCreateForm form) {
//		// 1. PHÂN TÍCH BUSINESS LOGIC (BLA)
//		List<String> receiverIds = analyzeReceivers(form);
//
//		if (receiverIds.isEmpty()) {
//			return 0; // Không có người nhận
//		}
//
//		// 2. TẠO VÀ LƯU BẢN GHI CHO TỪNG NGƯỜI NHẬN
//		List<Notification> newNotifications = new ArrayList<>();
//
//		for (String receiverId : receiverIds) {
//			// Tránh gửi thông báo cho chính người thực hiện hành động, trừ phi là thông báo hệ thống
//			if (form.getActorId() != null && form.getActorId().equals(receiverId) &&
//			    !isSystemNotification(form.getType())) {
//				continue;
//			}
//
//			Notification notification = Notification.builder()
//			    .id(UUID.randomUUID().toString())
//			    .receiverId(receiverId)
//			    .actorId(form.getActorId())
//			    .projectId(form.getProjectId())
//			    .taskId(form.getTaskId())
//			    .type(form.getType())
//			    .title(form.getTitle())
//			    .content(form.getContent())
//			    .isRead(false)
//			    .isDeleted(false)
//			    .build();
//
//			newNotifications.add(notification);
//		}
//
//		notificationRepository.saveAll(newNotifications);
//		return newNotifications.size();
//	}
	

}