package aws.todolist.notification.service;

import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.messaging.kafka.message.NotificationMessage;

public interface WebSocketService {

    public void sendGlobal(Notification notification) ;

    public void sendToUserNew(String email, Notification notification);
}
