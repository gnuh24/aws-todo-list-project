package aws.todolist.notification.dto.event.payload;

import aws.todolist.notification.dto.event.ActorDto;
import lombok.Data;

import java.util.List;

@Data
public abstract class PayloadBase {

    private String projectId;   // dùng để route websocket

    private ActorDto actor;     // ai gây ra event

    private List<String> receivers;   // null nếu không cần notification
}
