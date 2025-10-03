package com.ec.catalog.exceptions.business;


public abstract class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}
