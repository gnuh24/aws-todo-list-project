package aws.todoist.websocket.service;

import aws.todoist.websocket.dto.notification.NotificationResponse;

public interface WebSocketService {

    public void sendProject() ;

    public void sendNotification(NotificationResponse notification);
}
