package aws.todoist.websocket.dto.event.payload;

import aws.todoist.websocket.dto.event.dto.MemberEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPayload extends PayloadBase{

    private MemberEventDto memberEventDto;
}
