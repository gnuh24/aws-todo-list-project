package aws.todolist.chat.helper;

public class ChatHelper {
    public static String getChatId(String senderId, String recipientId) {
        // Thêm check null để tránh sập server
        if (senderId == null || recipientId == null) {
            throw new IllegalArgumentException("SenderId and RecipientId must not be null");
        }

        // Logic tạo Chat Room ID (Ví dụ: A_B hoặc B_A luôn giống nhau)
        if (senderId.compareTo(recipientId) < 0) {
            return senderId + "_" + recipientId;
        } else {
            return recipientId + "_" + senderId;
        }
    }
}