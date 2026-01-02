package aws.todolist.chat.controller;

import aws.todolist.chat.entity.ChatMessage;
import aws.todolist.chat.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    SimpMessagingTemplate messagingTemplate;
    ChatService chatService;

    // 1. Nhận tin nhắn từ Client gửi tới: /app/chat
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage){
        chatMessage.setTimeStamp(new Date());

        // a. Lưu tin nhắn vào DB
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);

        // b. Gửi tin nhắn tới người nhận (Real-time)
        // Client người nhận sẽ subscribe: /user/{userId}/queue/messages
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/messages",
                savedMessage
        );
    }

    // API lấy lịch sử chat (REST API)
    @GetMapping("/messages/{senderId}/{recipientId}")
    public List<ChatMessage> findChatMessage(@PathVariable String senderId, @PathVariable String recipientId){
        return chatService.findChatMessages(senderId, recipientId);
    }

}
