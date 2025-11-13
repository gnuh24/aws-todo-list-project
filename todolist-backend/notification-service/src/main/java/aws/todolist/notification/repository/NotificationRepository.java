package aws.todolist.notification.repository;

import aws.todolist.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    // 1. Lấy tất cả thông báo (chưa bị soft delete) cho một người nhận
// Phương thức mới sử dụng JPQL để xử lý tham số isRead là NULL
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :receiverId AND n.isDeleted = false " +
	"AND (:isRead IS NULL OR n.isRead = :isRead)" +
			"ORDER BY n.createdAt DESC, n.id DESC")
    Page<Notification> findNotificationsByFilter(
	@Param("receiverId") String receiverId,
	@Param("isRead") Boolean isRead,
	Pageable pageable
    );
	@Modifying
	@Query("UPDATE Notification n SET n.isRead = :isRead, n.readAt = CASE WHEN :isRead = true THEN CURRENT_TIMESTAMP ELSE NULL END " +
	    "WHERE n.id = :notificationId AND n.isDeleted = false") // Đã bỏ AND n.receiver.id = :receiverId
	int updateReadStatus(
	    @Param("notificationId") String notificationId,
	    @Param("isRead") boolean isRead // Chỉ còn 2 tham số
	);

	@Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.id = :receiverId AND n.isRead = :isRead")
	Long getCountNotificationByIsRead(@Param("isRead") boolean isRead, @Param("receiverId") String receiverId);

	@Modifying
	@Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.id IN :ids")
	Integer markAllAsRead(@Param("ids") List<String> ids, @Param("isRead") boolean isRead);
}