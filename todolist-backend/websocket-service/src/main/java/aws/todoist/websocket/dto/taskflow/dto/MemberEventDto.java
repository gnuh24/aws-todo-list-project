package aws.todoist.websocket.dto.taskflow.dto;

import aws.todoist.websocket.enums.eventDto.Role;
import aws.todoist.websocket.enums.eventDto.StatusMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEventDto {

    private String id;

    private String accountId;

    private String displayName;

    private String avatar;

    private Role role;

    private String email;

    private StatusMember status;
}
