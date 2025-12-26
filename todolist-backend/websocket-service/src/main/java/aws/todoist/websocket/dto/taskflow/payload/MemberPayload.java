package aws.todoist.websocket.dto.taskflow.payload;

import aws.todoist.websocket.dto.taskflow.dto.MemberEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPayload extends PayloadBase{

    private MemberEventDto memberEventDto;
}
