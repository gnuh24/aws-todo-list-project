package com.ec.order.exceptions.business.order;


public class OrderNotFoundException extends OrderException {
	public OrderNotFoundException(String orderId) {
		super("Không tìm thấy đơn hàng với ID: " + orderId);
	}
}
