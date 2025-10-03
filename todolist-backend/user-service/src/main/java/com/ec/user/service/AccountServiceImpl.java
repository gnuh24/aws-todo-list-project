package com.ec.user.service;


import com.ec.user.dto.account.AccountCreateForm;
import com.ec.user.entity.Account;
import com.ec.user.entity.Profile;
import com.ec.user.integration.redis.RedisService;
import com.ec.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ProfileService profileService;

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
	
	//    @Autowired
//    private EmailService emailService;
//
	@Autowired
	private RedisService redisService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Account with username " + username + " not found"));
	}
	
	@Override
	public Account getAccountById(String accountId) {
		return accountRepository.findById(accountId)
		    .orElseThrow(() -> new UsernameNotFoundException("Account with accountId " + accountId + " not found"));
	}
	
	@Override
	public Account getAccountByUsername(String username) {
		return accountRepository.findByUsername(username).orElse(null);
//                .orElseThrow(() -> new UsernameNotFoundException("Account with username " + username + " not found"));
	}


//
//    @Override
//    public Page<Account> getAllAccounts(Pageable pageable, AccountFilterForm filterForm) {
//        Specification<Account> specification = AccountSpecification.buildWhere(filterForm);
//        return accountRepository.findAll(specification, pageable);
//    }
	
	
	@Override
	@Transactional
	public Account createAccount(AccountCreateForm accountCreateForm, Profile profile) {
		
		Account account = new Account();
		account.setId(accountCreateForm.getId());
		account.setUsername(accountCreateForm.getUsername());
		account.setPassword(passwordEncoder.encode(accountCreateForm.getPassword()));
		account.setProfile(profile);
		
		account = accountRepository.save(account);
		
		return account;
	}
	
	@Override
	public Account activeAccount(String accountId) {
		Account account = getAccountById(accountId);
		account.setStatus(Account.Status.ACTIVE);
		return accountRepository.save(account);
	}
	
	@Override
	public Account updatePassword(String username, String newPassword) {
		Account account = getAccountByUsername(username);
		return updatePassword(account, newPassword);
	}
	
	@Override
	public Account updatePassword(Account account, String newPassword) {
		account.setPassword(newPassword);
		return accountRepository.save(account);
	}
	
	@Override
	public Account updateUsername(Account account, String newEmail) {
		account.setUsername(newEmail);
		return accountRepository.save(account);
	}

//    @Override
//    public Account updateStatusOfAccount(String accountId, Account.Status status) {
//        Account account = getAccountById(accountId);
//        account.setStatus(status);
//        return accountRepository.save(account);
//    }
//
//    @Override
//    public Account updateRoleOfAccount(String accountId, Account.Role role) {
//        Account account = getAccountById(accountId);
//        account.setRole(role);
//        return accountRepository.save(account);
//    }

}

