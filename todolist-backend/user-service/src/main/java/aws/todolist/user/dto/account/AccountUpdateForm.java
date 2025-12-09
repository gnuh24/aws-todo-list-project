package aws.todolist.user.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUpdateForm {

    @Size(max = 512, message = "Avatar URL không được vượt quá 512 ký tự")
    private String avatar;

    @Size(max = 255, message = "Display name không được vượt quá 255 ký tự")
    private String displayName;

    @Schema(description = "Cập nhật người dùng có muốn nhận thông báo không")
    private Boolean receiveEmail;
}
