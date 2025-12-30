package aws.todolist.project.dto.event;

import aws.todolist.project.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {

    private EventType eventType;

    private String version;        // "v1"

    private Instant occurredAt;    // thời điểm event xảy ra

    private T payload;             // nội dung thực
}
