package aws.todolist.taskflow.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
	
	@Schema(description = "Mã ID account của member", example = "uuid-account")
	private String id;
	
	@Schema(description = "Tên hiển thị của member", example = "Nguyen Van A")
	private String displayName;
	
	@Schema(description = "URL avatar của member", example = "https://example.com/avatar.jpg")
	private String avatar;
	
	@Schema(description = "email", example = "abv@gmail.com")
	private String email;
	
}
