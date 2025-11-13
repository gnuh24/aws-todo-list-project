package aws.todolist.notification.dto.notification;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class UpdateMoreIdRequest {

    private List<String> ids;

    private Boolean isRead;

}
