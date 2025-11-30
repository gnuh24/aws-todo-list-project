package aws.todolist.notification.controller;


import aws.todolist.notification.api.ApiResponse;
import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.dto.notification.UpdateMoreIdRequest;
import aws.todolist.notification.dto.notification.UpdateReadStatusRequest;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
	
	private final NotificationService notificationService;
	
	// Phương thức Helper để ánh xạ Entity sang DTO
	private NotificationResponse mapToResponse(Notification notification) {
		// Logic mapping...
		return NotificationResponse.builder()
		    .id(notification.getId())
		    .title(notification.getTitle())
		    .content(notification.getContent())
		    .type(notification.getType())
		    .isRead(notification.isRead())
		    .createdAt(notification.getCreatedAt())
		    // Lấy ID từ entity
		    .actorId(notification.getActor() != null ? notification.getActor().getId() : null)
		    .projectId(notification.getProjectId())
		    .taskId(notification.getTaskId())
		    .displayName(notification.getActor().getDisplayName())
		    .avatar(notification.getActor().getAvatar())
		    .build();
	}
	
	@GetMapping("/my-notification")
	public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
	    Pageable pageable,
	    @RequestParam(value = "isRead", required = false) Boolean isRead,
	    @RequestHeader("X-User-Id") String receiverId
	
	) {
		
		// 2. Gọi Service: Truyền thêm tham số isRead
		Page<Notification> notifications =
		    notificationService.getNotifications(receiverId, isRead, pageable); // Đã thêm isRead
		
		// 3. Thực hiện ánh xạ (Mapping) từ Entity sang DTO
		Page<NotificationResponse> notificationsPage = notifications.map(this::mapToResponse);
		
		// 4. Đóng gói vào Custom ApiResponse và trả về ResponseEntity
		ApiResponse<Page<NotificationResponse>> response = new ApiResponse<>(
		    HttpStatus.OK.value(),
		    "Notifications retrieved successfully.",
		    notificationsPage
		);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@GetMapping("/count-my-notification-unread")
	public ResponseEntity<ApiResponse<Long>> countMyNotificationsByIsRead(
	    @RequestParam(value = "isRead", required = false) Boolean isRead,
	    @RequestHeader("X-User-Id") String receiverId
	) {
		
		
		// 2. Gọi Service: Truyền thêm tham số isRead
		Long count = notificationService.getCountNotificationByIsRead(isRead, receiverId); // Đã thêm isRead
		
		
		// 4. Đóng gói vào Custom ApiResponse và trả về ResponseEntity
		ApiResponse<Long> response = new ApiResponse<>(
		    HttpStatus.OK.value(),
		    "Notifications retrieved successfully.",
		    count
		);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@PatchMapping("/read-status/bulk")
	public ResponseEntity<ApiResponse<Integer>> markNotificationsAsRead(
	    @RequestBody @Valid UpdateMoreIdRequest request
	) {
		Integer updatedCount = notificationService.markNotificationsByIsRead(
		    request.getIds(),
		    request.getIsRead()
		);
		
		ApiResponse<Integer> response = new ApiResponse<>(
		    HttpStatus.OK.value(),
		    request.getIsRead() ? "Đánh dấu các thông báo là đã đọc" : "Đánh dấu các thông báo là chưa đọc",
		    updatedCount
		);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@PatchMapping("/{id}/read-status")
	public ResponseEntity<ApiResponse<String>> updateReadStatus(
	    @PathVariable("id") String notificationId,
	    @RequestBody @Valid UpdateReadStatusRequest request
	) {
		// **Đã bỏ việc lấy receiverId từ SecurityContext và truyền vào Service.**
		
		int updatedCount = notificationService.updateReadStatus(
		    notificationId,
		    request.getIsRead()
		);
		
		String action = request.getIsRead() ? "marked as read" : "marked as unread";
		String message;
		HttpStatus status;
		
		if (updatedCount > 0) {
			message = "Notification '" + notificationId + "' successfully " + action + ".";
			status = HttpStatus.OK;
		} else {
			// Thông báo không tồn tại hoặc đã ở trạng thái đó rồi.
			message = "Notification '" + notificationId + "' could not be updated or was already in that state.";
			status = HttpStatus.BAD_REQUEST;
		}
		
		ApiResponse<String> response = new ApiResponse<>(
		    status.value(),
		    message,
		    null
		);
		return new ResponseEntity<>(response, status);
	}
	
	
}