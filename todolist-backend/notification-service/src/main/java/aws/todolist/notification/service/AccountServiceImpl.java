package aws.todolist.notification.service;

import aws.todolist.notification.entity.Account;
import aws.todolist.notification.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
	}

	@Override
	public Account getAccountByEmail(String email) {
		return accountRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
	}

	@Override
	public Account getAccountById(String accountId) {
		return accountRepository.findById(accountId)
				.orElseThrow(() -> new UsernameNotFoundException("Account with accountId " + accountId + " not found"));
	}


}

