package aws.todolist.user.service;


import aws.todolist.user.dto.account.AccountUpdateForm;
import aws.todolist.user.entity.Account;
import aws.todolist.user.integration.redis.RedisService;
import aws.todolist.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	

//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private OTPService otpService;
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RedisService redisService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
	}
	
	
	@Override
	public Account getAccountById(String accountId) {
		return accountRepository.findById(accountId)
		    .orElseThrow(() -> new UsernameNotFoundException("Account with accountId " + accountId + " not found"));
	}
	
	@Override
	public Account getAccountByUsername(String username) {
		return accountRepository.findByEmail(username).orElse(null);
//                .orElseThrow(() -> new UsernameNotFoundException("Account with username " + username + " not found"));
	}
	
	@Override
	public Account updateAccount(Account account, AccountUpdateForm form) {
	
		
		// Cập nhật avatar nếu có truyền
		if (form.getAvatar() != null && !form.getAvatar().isBlank()) {
			account.setAvatar(form.getAvatar());
		}
		
		// Cập nhật displayName nếu có truyền
		if (form.getDisplayName() != null && !form.getDisplayName().isBlank()) {
			account.setDisplayName(form.getDisplayName());
		}

		// Cập nhật receiveEmail
		if (form.getReceiveEmail() != null){
			account.setReceiveEmail(form.getReceiveEmail());
		}
		
		return accountRepository.save(account);
	}



//
//    @Override
//    public Page<Account> getAllAccounts(Pageable pageable, AccountFilterForm filterForm) {
//        Specification<Account> specification = AccountSpecification.buildWhere(filterForm);
//        return accountRepository.findAll(specification, pageable);
//    }
	

	
}

