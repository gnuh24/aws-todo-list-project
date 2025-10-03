package com.ec.order.exceptions.business.payment;

public class PaymentNotFoundException extends PaymentException {
	public PaymentNotFoundException(String orderId) {
		super("Không tìm thấy  payment với ID: " + orderId);
	}
}
