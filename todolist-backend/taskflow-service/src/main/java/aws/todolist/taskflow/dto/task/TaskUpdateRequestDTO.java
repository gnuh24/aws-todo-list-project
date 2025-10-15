package aws.todolist.taskflow.dto.task;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO để cập nhật thông tin chung cho task")
public class TaskUpdateRequestDTO {

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime deadline;

    private String priority; // Enum Priority → gửi dạng String ("LOW", "MEDIUM", "HIGH", ...)

    private Boolean isPinned;

    private boolean isStartTimeSent;

    private boolean isDeadlineSent;

    @JsonSetter("startTime")
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.isStartTimeSent = true;
    }

    @JsonSetter("deadline")
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        this.isDeadlineSent = true;
    }
}
