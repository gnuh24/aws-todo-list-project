package aws.todolist.notification.mapper;

import aws.todolist.notification.dto.eventNotification.NotificationEventPayload;
import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.dto.notification.WebSocketResponse;
import aws.todolist.notification.entity.Notification;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
	
	NotificationResponse toResponse(Notification notification);

	@Mapping(target = "notification", source = "notificationResponse")
	@Mapping(target = "email", source = "email")
	NotificationEventPayload toEventPayload(
			String email,
			NotificationResponse notificationResponse
	);

	@AfterMapping
	default void handleActorFields(Notification notification, @MappingTarget NotificationResponse dto) {
		if (notification.getActor() != null) {
			dto.setActorId(notification.getActor().getId());
			dto.setDisplayName(notification.getActor().getDisplayName());
			dto.setAvatar(notification.getActor().getAvatar());
		}
	}

	
	// Map List<Notification> → List<NotificationResponse>
	List<NotificationResponse> toResponseList(List<Notification> notifications);
	
	// Map Page<Notification> → Page<NotificationResponse>
	default Page<NotificationResponse> toResponsePage(Page<Notification> page) {
		List<NotificationResponse> dtoList = toResponseList(page.getContent());
		return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
	}


	WebSocketResponse toWebSocketResponse(Notification notification);

	@AfterMapping
	default void handleActorFieldsWebSocket(Notification notification, @MappingTarget WebSocketResponse dto) {
		if (notification.getActor() != null) {
			dto.setActorId(notification.getActor().getId());
			dto.setDisplayName(notification.getActor().getDisplayName());
			dto.setAvatar(notification.getActor().getAvatar());
			dto.setReceiverId(notification.getReceiver().getId());
		}
	}


}
