package aws.todolist.user.controller;
import aws.todolist.user.logging.AppLogger;
import aws.todolist.user.dto.account.AccountDetailResponseDTO;
import aws.todolist.user.entity.Account;
import aws.todolist.user.mapper.AccountMapper;
import aws.todolist.user.service.AccountService;
import aws.todolist.user.utils.EnvironmentUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private AccountService accountService;
	
	@MockitoBean
	private AccountMapper accountMapper;
	
	@MockitoBean
	private AppLogger appLogger;
	
	@MockitoBean
	private EnvironmentUtils environmentUtils;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}
	
	// --- SETUP DỮ LIỆU CHUNG ---
	private Account mockAccount;
	private AccountDetailResponseDTO mockResponseDTO;
	private final String MOCK_USER_ID = "22222222-2222-2222-2222-222222222222";
	
	@BeforeEach
	void setupData() {
		// Khởi tạo dữ liệu giả lập để dùng chung cho các test case
		mockAccount = new Account();
		mockAccount.setId(MOCK_USER_ID);
		mockAccount.setEmail("test@aws.com");
		mockAccount.setDisplayName("Test User");
		
		mockResponseDTO = new AccountDetailResponseDTO();
		mockResponseDTO.setId(MOCK_USER_ID);
		mockResponseDTO.setEmail("test@aws.com");
		mockResponseDTO.setDisplayName("Test User");
	}
	
	// --- CASE 1: LẤY THÔNG TIN THÀNH CÔNG (HAPPY CASE) ---
	@Test
	void getAccountInfo_WhenAccountExists_ShouldReturn200AndData() throws Exception {
		// 1. GIVEN (Giả lập hành vi của Service và Mapper)
		// Khi gọi service lấy ID này -> Trả về account mock
		Mockito.when(accountService.getAccountById(MOCK_USER_ID)).thenReturn(mockAccount);
		// Khi gọi mapper convert -> Trả về DTO mock
		Mockito.when(accountMapper.entityToDetailDTO(mockAccount)).thenReturn(mockResponseDTO);
		
		// 2. WHEN & THEN (Thực hiện gọi API và kiểm tra)
		mockMvc.perform(get("/v1/accounts/me")
				.header("X-User-Id", MOCK_USER_ID) // Giả lập Header user id
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON))
			
			// Kiểm tra HTTP Status = 200 OK
			.andExpect(status().isOk())
			
			// Kiểm tra cấu trúc JSON trả về (ApiResponse)
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value("Lấy thông tin account thành công"))
			
			// Kiểm tra dữ liệu bên trong (Data)
			.andExpect(jsonPath("$.data.id").value(MOCK_USER_ID))
			.andExpect(jsonPath("$.data.email").value("test@aws.com"));
	}
	
	// --- CASE 2: KHÔNG TÌM THẤY ACCOUNT (EXCEPTION CASE) ---
	@Test
	void getAccountInfo_WhenAccountNotFound_ShouldReturn404() throws Exception {
		// GIVEN
		String nonExistId = "22222222-2222-2222-2222-222222222223";

		Mockito.when(accountService.getAccountById(nonExistId))
			.thenThrow(new UsernameNotFoundException("Account not found"));

		// WHEN & THEN
		mockMvc.perform(get("/v1/accounts/me")
				.header("X-User-Id", nonExistId)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Account not found"))
			.andExpect(jsonPath("$.errorCode").value("ACCOUNT_PROFILE_NOT_FOUND"));
	}
	
	
	
}

