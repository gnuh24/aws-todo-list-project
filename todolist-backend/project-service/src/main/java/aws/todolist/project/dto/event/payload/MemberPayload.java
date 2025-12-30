package aws.todolist.project.dto.event.payload;

import aws.todolist.project.dto.event.PayloadBase;
import aws.todolist.project.dto.event.dto.MemberEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPayload extends PayloadBase {

    private MemberEventDto memberEventDto;
}
