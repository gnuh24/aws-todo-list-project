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


    @Override
    public void sendProject() {

    }

    @Override
    public void sendNotification(NotificationResponse notification) {

        // Kiểm tra user có đang kết nối WebSocket không
        SimpUser simpUser = simpUserRegistry.getUser(notification.);
        if (simpUser == null) {
            System.err.println("❌ User " + accountId + " chưa kết nối WebSocket hoặc chưa được setUser(authentication)");
            return;
        }

        System.out.println("✅ User " + accountId + " đang online qua WebSocket, sessions: " + simpUser.getSessions().size());

        String destination = "/private";
        messagingTemplate.convertAndSendToUser(accountId, destination, mapper.toResponse(notification));
    }
}
