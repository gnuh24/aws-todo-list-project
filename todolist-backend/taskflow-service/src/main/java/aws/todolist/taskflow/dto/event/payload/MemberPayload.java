package aws.todolist.taskflow.dto.event.payload;

import aws.todolist.taskflow.dto.event.PayloadBase;
import aws.todolist.taskflow.dto.event.dto.MemberEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPayload extends PayloadBase {

    private MemberEventDto memberEventDto;
}
