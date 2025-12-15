package aws.todolist.user.controller;

import aws.todolist.user.api.ApiResponse;
import aws.todolist.user.dto.account.AccountDetailResponseDTO;
import aws.todolist.user.dto.account.AccountUpdateForm;
import aws.todolist.user.entity.Account;
import aws.todolist.user.mapper.AccountMapper;
import aws.todolist.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import aws.todolist.user.exceptionHandler.exceptions.AccountNotFoundException;

@RestController
@RequestMapping("/v1/accounts")
@Tag(name = "Account", description = "Quản lý thông tin tài khoản người dùng")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Operation(summary = "Lấy chi tiết account đang đăng nhập",
	    description = "Chỉ admin hoặc chính người dùng mới có thể xem thông tin account của mình")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<AccountDetailResponseDTO>> getAccountInfo(
	    @RequestHeader("X-User-Id") String accountId
	) throws AccountNotFoundException {
		Account account = accountService.getAccountById(accountId);
		AccountDetailResponseDTO accountDTO1 = accountMapper.entityToDetailDTO(account);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin account thành công", accountDTO1));
	}
	

	@GetMapping()
	public ResponseEntity<ApiResponse<AccountDetailResponseDTO>> getAccountInfoByEmail(
		@RequestParam("email") String email
	) throws AccountNotFoundException {
		Account account = accountService.getAccountByUsername(email);
		AccountDetailResponseDTO accountDTO1 = accountMapper.entityToDetailDTO(account);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin account thành công", accountDTO1));
	}
	
	@Operation(summary = "Cập nhật account cá nhân",
	    description = "Cập nhật thông tin tài khoản của người dùng đang đăng nhập")
	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<AccountDetailResponseDTO>> updateAccount(
	    @RequestBody @Valid AccountUpdateForm form,
	    @RequestHeader("X-User-Id") String accountId
	) throws AccountNotFoundException {
		
		Account account = accountService.getAccountById(accountId);
		Account updatedAccount = accountService.updateAccount(account, form);
		AccountDetailResponseDTO responseDTO = accountMapper.entityToDetailDTO(updatedAccount);
		
		return ResponseEntity.ok(
		    new ApiResponse<>(200, "Cập nhật account thành công", responseDTO)
		);
	}
}
