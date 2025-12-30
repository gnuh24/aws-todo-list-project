package aws.todolist.media.repository;

import aws.todolist.media.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
	
	boolean existsByEmail(String email);
	Optional<Account> findByEmail(String email);
}

