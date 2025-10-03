package com.ec.catalog.exceptions.business.product_variant;

public class ProductVariantQuantityNotEnough extends ProductVariantException {
	public ProductVariantQuantityNotEnough(String variantId, int requested, int available) {
		super("Số lượng trong kho không đủ cho phiên bản ID: " + variantId
		    + ". Yêu cầu: " + requested + ", hiện có: " + available);
	}
}
