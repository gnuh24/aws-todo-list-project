package aws.todolist.auth.repository;

import aws.todolist.auth.entity.AccountRecoveryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRecoveryKeyRepository extends JpaRepository<AccountRecoveryKey, String> {

    Optional<AccountRecoveryKey> findByKeyHashAndIsUsedFalse(String keyHash);
	List<AccountRecoveryKey> findAllByAccountIdAndIsUsedFalse(String accountId);
    long countByAccountIdAndIsUsedFalse(String accountId);
	void deleteByAccountId(String accountId);
}
