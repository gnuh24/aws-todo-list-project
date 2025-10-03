package com.ec.catalog.exceptions.business.product_variant;

public class ProductVariantNotFoundException extends ProductVariantException {
	public ProductVariantNotFoundException(String variantId) {
		super("Không tìm thấy phiên bản sản phẩm với ID: " + variantId);
	}
}
