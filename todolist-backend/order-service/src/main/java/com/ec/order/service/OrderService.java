package com.ec.order.service;

import com.ec.order.dto.order.CheckoutRequest;
import com.ec.order.dto.order.VnPayPaymentCreateForm;
import com.ec.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
	Page<Order> getMyOrders(String accountId, Pageable pageable);
	Order getOrderDetailById(String orderId, String accountId);
	String createOrderWithCOD(String accountId, CheckoutRequest request);
	
	Order createOrderAndGenerateVnPayUrl(String accountId, CheckoutRequest request);
	Order processVnPayReturn(VnPayPaymentCreateForm form);
	
	
}
