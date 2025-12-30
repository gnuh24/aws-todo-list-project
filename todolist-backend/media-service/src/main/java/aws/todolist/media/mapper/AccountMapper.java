package aws.todolist.media.mapper;

import aws.todolist.media.dto.account.AccountDetailResponseDTO;
import aws.todolist.media.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

	@Mapping(target = "role", expression = "java(account.getRole().name())")
	@Mapping(target = "status", expression = "java(account.getStatus().name())")
	@Mapping(target = "twoFactorEnabled", expression = "java(account.isTwoFactorEnabled())")
	AccountDetailResponseDTO entityToDetailDTO(Account account);
	
}
