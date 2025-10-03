package com.ec.order.repository;

import com.ec.order.entity.Order;
import com.ec.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
	
	Optional<Payment> findByOrder(Order order);
	
}
