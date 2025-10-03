package com.ec.order.exceptions.business.payment;


import com.ec.order.exceptions.business.BusinessException;

public abstract class PaymentException extends BusinessException {
	
	public PaymentException(String message) {
		super(message);
	}
	
}
