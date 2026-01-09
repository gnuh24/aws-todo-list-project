package aws.todolist.taskflow.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorDto {

    private String id;

    private String name;

    private String clientId;
}
