package aws.todolist.notification.service;


import aws.todolist.notification.entity.NotificationSetting;
import aws.todolist.notification.logging.AppLogger;
import aws.todolist.notification.entity.Account;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.mapper.NotificationMapper;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;
import aws.todolist.notification.repository.AccountRepository;
import aws.todolist.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
	
	private final NotificationRepository notificationRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AppLogger appLogger;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PublishEventService publishEventService;

	@Autowired
	private NotificationSettingService notificationSettingService;


	
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

	@Override
	public Long getCountNotificationByIsRead(boolean isRead, String idReceiver) {
		return notificationRepository.getCountNotificationByIsRead(isRead, idReceiver);
	}

	@Override
	public Integer markNotificationsByIsRead(List<String> ids, boolean isRead) {
		if (ids == null || ids.isEmpty()) return 0;
		return notificationRepository.markAllAsRead(ids, isRead);
	}


	@Override
	public void create(NotificationMessage msg) {
		try {
			// Tìm actor và receiver trong DB
			Account receiver = accountService.getAccountByEmail(msg.getReceiverEmail());

			Account actor = accountService.getAccountById(msg.getActorId());

			NotificationSetting notificationSetting = notificationSettingService.getByAccountIdAndType(receiver, msg.getType());

			if (!notificationSetting.isEnableWeb()){
				return;
			}

			Notification notification = Notification.builder()
			    .id(UUID.randomUUID().toString())
			    .receiver(receiver)
			    .actor(actor)
			    .projectId(msg.getProjectId())
			    .taskId(msg.getTaskId())
			    .type(msg.getType())
			    .title(msg.getTitle())
			    .content(msg.getContent())
			    .read(false)
			    .createdAt(LocalDateTime.now())
			    .isDeleted(false)
			    .build();

			notificationRepository.save(notification);

			System.err.println("💾 [NotificationService] Saved notification "
			    + msg.getType() + " for receiver " + msg.getReceiverEmail());


//			// Kiểm tra xem người dùng có muốn gửi thông báo đến email hay không
			if (notificationSetting.isEnableEmail()){
				emailService.sendNotification(notification);
			}

			// send event to websocket
			publishEventService.publish(notification);


		} catch (Exception e) {
			System.err.println("❌ Lỗi khi tạo Notification: " + e.getMessage());
			e.printStackTrace();
		}
	}




	
	
}