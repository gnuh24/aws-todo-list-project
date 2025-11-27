package aws.todolist.auth.service;


import aws.todolist.auth.dto.account.AccountCreateForm;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.integration.redis.RedisService;
import aws.todolist.auth.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
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


//
//    @Override
//    public Page<Account> getAllAccounts(Pageable pageable, AccountFilterForm filterForm) {
//        Specification<Account> specification = AccountSpecification.buildWhere(filterForm);
//        return accountRepository.findAll(specification, pageable);
//    }
	
	
	@Override
	@Transactional
	public Account saveAccount(AccountCreateForm form) {
		Account account = new Account();
		
		account.setId(form.getId());
		account.setEmail(form.getEmail());
		account.setPassword(form.getPassword());
		account.setAvatar(form.getAvatar());

		return accountRepository.save(account);
	}
	
	@Override
	public Account saveAccount(Account account) {
		return accountRepository.save(account);
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
		account.setPassword(passwordEncoder.encode(newPassword));
		return accountRepository.save(account);
	}
	
	@Override
	public Account updateEmail(Account account, String newEmail) {
		account.setEmail(newEmail);
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

