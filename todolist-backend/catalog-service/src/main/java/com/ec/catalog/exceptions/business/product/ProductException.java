package com.ec.catalog.exceptions.business.product;

import com.ec.catalog.exceptions.business.BusinessException;

// 3. ProductException: lỗi liên quan đến sản phẩm
public abstract class ProductException extends BusinessException {
	public ProductException(String message) {
		super(message);
	}
}
