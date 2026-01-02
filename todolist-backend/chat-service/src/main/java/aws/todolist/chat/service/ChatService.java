package aws.todolist.chat.service;

import aws.todolist.chat.entity.ChatMessage;
import aws.todolist.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import aws.todolist.chat.helper.ChatHelper;

import java.util.List;

import static aws.todolist.chat.helper.ChatHelper.getChatId;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage chatMessage){
        // Tạo chatId để gom nhóm tin nhắn giữa 2 người
        // Ví dụ: userA chat với userB -> chatId luôn là "userA_userB" (sắp xếp theo alphabet để đồng nhất)
        String chatId = getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId());
        chatMessage.setChatId(chatId);
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId){
        String chatId = getChatId(senderId, recipientId);
        return chatMessageRepository.findByChatId(chatId);
    }
}
