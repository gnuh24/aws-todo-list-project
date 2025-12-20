package aws.todolist.notification.dto.event.dto;

import aws.todolist.notification.enums.Role;
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
}
