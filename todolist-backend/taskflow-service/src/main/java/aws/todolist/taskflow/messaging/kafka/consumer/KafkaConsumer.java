package aws.todolist.taskflow.messaging.kafka.consumer;

import aws.todolist.taskflow.service.ServiceInterface.ProjectService;
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
    private ProjectService projectService;

    @KafkaListener(
            topics = "${app.kafka.topic.auth.initialize-default-project}",
            groupId = "project-service"
    )
    public void consume(String message) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode json = objectMapper.readTree(message);

        String accountId = json.get("accountId").asText();
        String email = json.get("email").asText();


        projectService.addProjectDefault(accountId);

    }

}
