package com.ec.catalog.exceptions.business.product;

public class ProductAlreadyExistsException extends ProductException {
	public ProductAlreadyExistsException(String name) {
		super("Sản phẩm với tên '" + name + "' đã tồn tại.");
	}
}
