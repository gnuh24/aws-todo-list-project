package com.ec.catalog.exceptions.business.product_variant;

import com.ec.catalog.exceptions.business.BusinessException;

// Base exception
public abstract class ProductVariantException extends BusinessException {
	public ProductVariantException(String message) {
		super(message);
	}
}
