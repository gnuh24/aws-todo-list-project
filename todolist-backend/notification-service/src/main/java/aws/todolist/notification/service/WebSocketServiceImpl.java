package aws.todolist.notification.service;

import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Service
public class WebSocketServiceImpl implements WebSocketService{


    @Autowired
    private NotificationMapper mapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SimpUserRegistry simpUserRegistry;


    @Override
    public void sendGlobal(Notification notification) {
        messagingTemplate.convertAndSend("/global", mapper.mapToResponse(notification));
    }


    public void sendToUserNew(String email, Notification notification) {

        // Kiểm tra user có đang kết nối WebSocket không
        SimpUser simpUser = simpUserRegistry.getUser(email);
        if (simpUser == null) {
            System.err.println("❌ User " + email + " chưa kết nối WebSocket hoặc chưa được setUser(authentication)");
            return;
        }

        System.out.println("✅ User " + email + " đang online qua WebSocket, sessions: " + simpUser.getSessions().size());

        String destination = "/private";
        messagingTemplate.convertAndSendToUser(email,destination, mapper.mapToResponse(notification));
    }


}
