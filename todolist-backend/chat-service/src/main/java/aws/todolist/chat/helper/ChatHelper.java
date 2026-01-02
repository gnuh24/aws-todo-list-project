package aws.todolist.chat.helper;

public class ChatHelper {
    public static String getChatId(String senderId, String recipientId){
        if(senderId.compareTo(recipientId) < 0){
            return senderId + "_" + recipientId;
        }else{
            return recipientId + "_" + senderId;
        }
    }
}
