package aws.todolist.taskflow.dto.section;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionUpdateRequestDTO {

    @NotNull(message = "Không được để trống position")
    @Min(value = 1, message = "Position phải lớn hơn hoặc bằng 1")
    private int position;
}
