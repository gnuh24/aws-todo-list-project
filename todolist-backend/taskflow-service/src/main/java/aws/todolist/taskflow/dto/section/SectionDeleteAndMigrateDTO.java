package aws.todolist.taskflow.dto.section;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionDeleteAndMigrateDTO {

    @NotBlank(message = "Không để trống id của section nguồn")
    private String idSectionSource;

    @NotBlank(message = "Không để trống id của section đích")
    private String idSectionDestination;

}
