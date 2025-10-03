package com.ec.order.repository;

import com.ec.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
	Page<Order> findByAccountIdAndIsTempFalse(String accountId, Pageable pageable);
	
	Optional<Order> findByIdAndAccountId(String orderId, String accountId);
}
