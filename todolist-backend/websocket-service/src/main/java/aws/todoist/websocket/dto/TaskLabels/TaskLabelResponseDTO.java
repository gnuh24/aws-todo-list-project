package aws.todoist.websocket.dto.TaskLabels;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin nhãn (label) của Task")
public class TaskLabelResponseDTO {
	
	@Schema(description = "Mã ID của TaskLabel", example = "aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
	private String id;
	
	@Schema(description = "Tên nhãn", example = "Database")
	private String name;
	
	@Schema(description = "Được AI gán hay không", example = "true")
	private Boolean isAiGenerated;
	
	@Schema(description = "Mức độ confidence của AI khi gán nhãn", example = "0.95")
	private Float confidence;
}
