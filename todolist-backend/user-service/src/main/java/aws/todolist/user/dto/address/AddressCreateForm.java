package aws.todolist.user.dto.address;

import lombok.Data;

@Data
public class AddressCreateForm {
	
	private String address;
	
	private String fullName;
	
	private String phone;
	
}
