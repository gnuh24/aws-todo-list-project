package aws.todolist.user.mapper;

import aws.todolist.user.dto.account.AccountDetailResponseDTO;
import aws.todolist.user.entity.Account;
import aws.todolist.user.service.AccountService;
import aws.todolist.user.service.AccountServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

	@Mapping(target = "role", expression = "java(account.getRole().name())")
	@Mapping(target = "status", expression = "java(account.getStatus().name())")
	AccountDetailResponseDTO entityToDetailDTO(Account account);
	
}
