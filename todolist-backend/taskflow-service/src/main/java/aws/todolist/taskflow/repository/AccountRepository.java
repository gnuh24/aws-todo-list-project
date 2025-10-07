package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByIdAndIsDeletedFalse(String id);
}

