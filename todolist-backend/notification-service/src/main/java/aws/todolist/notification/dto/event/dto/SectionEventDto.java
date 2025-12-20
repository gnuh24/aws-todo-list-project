package aws.todolist.notification.dto.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionEventDto {

    private String id;
    private String name;
    private Integer position;
}
