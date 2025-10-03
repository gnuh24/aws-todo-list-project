package com.ec.catalog.exceptions.business.category;

import com.ec.catalog.exceptions.business.BusinessException;

public abstract class CategoryException extends BusinessException {
	public CategoryException(String message) {
		super(message);
	}
}
