package aws.todoist.websocket.service;

import aws.todoist.websocket.dto.notification.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServiceImpl implements WebSocketService{

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SimpUserRegistry simpUserRegistry;


    /**
     * Project-level events
     * VD: project renamed, archived
     */
    @Override
    public void sendProjectEvent(String projectId, Object event) {
        System.err.println(projectId);
        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId,
                event
        );
    }

    /**
     * Task realtime UI
     * VD: task created / updated / moved / completed
     */
    @Override
    public void sendTaskEvent(String projectId, Object event) {
        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId + "/task",
                event
        );
    }

    /**
     * Section realtime UI
     * VD: section created / renamed / reordered
     */
    @Override
    public void sendSectionEvent(String projectId, Object event) {
        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId + "/section",
                event
        );
    }

    /**
     * Member realtime UI
     * VD: member added / removed / role updated
     */
    @Override
    public void sendMemberEvent(String projectId, Object event) {
        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId + "/member",
                event
        );
    }

    /**
     * Comment realtime UI (theo task)
     */
    @Override
    public void sendCommentEvent(String projectId, Object event) {
        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId + "/comment",
                event
        );
    }

    /**
     * Notification riêng cho từng user
     */
    @Override
    public void sendNotificationToUser(String email, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notification",
                notification
        );
    }

    @Override
    public void sendProjectSummaryToUser(String email, Object event) {

        // Kiểm tra user có đang kết nối WebSocket không
        SimpUser simpUser = simpUserRegistry.getUser(email);
        if (simpUser == null) {
            System.err.println("❌ User " + email + " chưa kết nối WebSocket hoặc chưa được setUser(authentication)");
            return;
        }

        System.out.println("✅ User " + email + " đang online qua WebSocket, sessions: " + simpUser.getSessions().size());


        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/project-summary",
                event
        );
    }


}
