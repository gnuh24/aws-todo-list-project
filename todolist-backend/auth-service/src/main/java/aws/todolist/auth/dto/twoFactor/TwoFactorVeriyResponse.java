package aws.todolist.auth.dto.twoFactor;

import lombok.Data;

import java.util.List;

@Data
public class TwoFactorVeriyResponse {
	List<String> recoveryKeys;
}
