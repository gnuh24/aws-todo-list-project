package com.ec.news.exceptions.business;


public abstract class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}
