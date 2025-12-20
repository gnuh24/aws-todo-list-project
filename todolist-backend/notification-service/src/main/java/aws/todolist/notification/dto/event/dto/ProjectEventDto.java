package aws.todolist.notification.dto.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectEventDto {
    private String id;
    private String name;
    private Boolean isArchived;
}
