package aws.todolist.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ChatMessage {
    @Id
    String id;
    String chatId; // Ghép từ senderId + recipientId để định danh phòng chat
    String senderId;
    String recipientId;
    String content;
    Date timeStamp;
}
