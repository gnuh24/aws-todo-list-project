package aws.todolist.auth.mapper;

import aws.todolist.auth.dto.account.AccountCreateForm;
import aws.todolist.auth.dto.account.AccountRedisDTO;
import aws.todolist.auth.dto.auth.AuthResponseDTO;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.security.JwtTokenProvider;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "role", expression = "java(account.getRole().name())")
    @Mapping(target = "token", expression = "java(jwtTokenProvider.generateToken(account))")
    @Mapping(target = "refreshToken", expression = "java(jwtTokenProvider.generateRefreshToken(account))")
	AuthResponseDTO toAuthResponse(Account account, @Context JwtTokenProvider jwtTokenProvider);
	
	Account toAccount(AccountCreateForm form);
	
	AccountCreateForm toAccountCreateForm(AccountRedisDTO form);
	
}
