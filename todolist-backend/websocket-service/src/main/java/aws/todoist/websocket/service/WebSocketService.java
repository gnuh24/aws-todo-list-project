package aws.todoist.websocket.service;

import aws.todoist.websocket.dto.notification.NotificationResponse;

public interface WebSocketService {

    // ===== Project =====
    void sendProjectEvent(String projectId, Object event);

    // ===== Task =====
    void sendTaskEvent(String projectId, Object event);

    // ===== Section =====
    void sendSectionEvent(String projectId, Object event);

    // ===== Member =====
    void sendMemberEvent(String projectId, Object event);

    // ===== Comment =====
    void sendCommentEvent(String projectId, Object event);

    // ===== Notification (private) =====
    void sendNotificationToUser(String userId, NotificationResponse notification);

    // ✅ NEW: Project summary (sidebar)
    void sendProjectSummaryToUser(String userId, Object event);
}
