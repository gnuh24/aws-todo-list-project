package aws.todolist.media.messaging.kafka;


import aws.todolist.media.enums.EventType;
import aws.todolist.media.service.MediaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @Autowired
    private MediaService mediaService;


    @KafkaListener(
            topics = "${app.kafka.topic.comment-attach-events}",
            groupId = "media-service"
    )
    public void consume(String message) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(message);

        String eventType = root.get("eventType").asText();

        JsonNode payload = root.get("payload");

        String attachmentUrl = payload.get("attachmentUrl").asText();

        if (EventType.COMMENT_ATTACH_DELETED.toString().equals(eventType)) {
            // TODO: xử lý xóa file
            System.out.println("Delete comment attach file: " + attachmentUrl);

            mediaService.deleteByMediaId(attachmentUrl);
        }
    }

}
