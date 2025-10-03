package com.ec.catalog.repository;

import com.ec.catalog.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    boolean existsByUsername(String username);
    Optional<Account> findByUsername(String username);
}

