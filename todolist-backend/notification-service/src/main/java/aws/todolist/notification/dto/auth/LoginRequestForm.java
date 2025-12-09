package aws.todolist.notification.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestForm {
	
	@NotBlank(message = "Email không được để trống !!")
	@Email(message = "Email phải đúng định dạng !!")
	private String email;
	
	@NotBlank(message = "Mật khẩu không được để trống !!")
	private String password;
}
