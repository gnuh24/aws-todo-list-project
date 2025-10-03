package com.ec.order.exceptions.business;


public abstract class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}
