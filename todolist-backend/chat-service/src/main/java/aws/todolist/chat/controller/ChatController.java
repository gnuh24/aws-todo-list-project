package aws.todolist.chat.controller;

import aws.todolist.chat.entity.ChatMessage;
import aws.todolist.chat.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j // Sử dụng Logger của Lombok cho chuyên nghiệp thay vì System.out
public class ChatController {
    SimpMessagingTemplate messagingTemplate;
    ChatService chatService;

    // 1. Nhận tin nhắn từ Client gửi tới: /app/chat
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {

        // --- BƯỚC 1: VALIDATION & CHUẨN BỊ DỮ LIỆU ---
        if (principal == null) {
            log.error("❌ Error: User not authenticated (Principal is null)");
            return;
        }

        String userId = principal.getName();
        chatMessage.setSenderId(userId);

        // Gán thời gian hiện tại luôn để gửi cho Client hiển thị đúng giờ
        if (chatMessage.getTimeStamp() == null) {
            chatMessage.setTimeStamp(new Date());
        }

        log.info("📩 Processing msg from: {} to: {}", userId, chatMessage.getRecipientId());

        // --- BƯỚC 2: GỬI TIN NHẮN NGAY LẬP TỨC (REAL-TIME) ---
        // Ưu tiên gửi qua WebSocket trước để User 2 nhận được ngay.
        // Không chờ Database lưu xong mới gửi.
        try {
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId(),
                    "/queue/messages",
                    chatMessage
            );
            log.info("🚀 Sent to WebSocket successfully");
        } catch (Exception e) {
            log.error("❌ Failed to send via WebSocket: {}", e.getMessage());
        }

        // --- BƯỚC 3: LƯU VÀO DATABASE (PERSISTENCE) ---
        // Lưu sau cùng. Nếu DB lỗi (timeout/die) thì cũng không ảnh hưởng trải nghiệm chat.
        try {
            chatService.saveMessage(chatMessage);
            log.info("✅ Saved to MongoDB");
        } catch (Exception e) {
            log.error("⚠️ Save DB Failed (User may have received msg, but not saved): {}", e.getMessage());
            // Ở đây có thể thêm logic đẩy vào Kafka hoặc Retry Queue nếu cần
        }
    }

    // API lấy lịch sử chat (REST API)
    @GetMapping("/messages/{senderId}/{recipientId}")
    public List<ChatMessage> findChatMessage(@PathVariable String senderId, @PathVariable String recipientId){
        return chatService.findChatMessages(senderId, recipientId);
    }
}