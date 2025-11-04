package aws.todolist.user.dto.account;

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
}
