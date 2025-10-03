package com.ec.order.exceptions.business.order;


public class OrderOutOfStockException extends OrderException {
	public OrderOutOfStockException(String productVariantId, int available, int requested) {
		super("Không đủ số lượng để tạo đơn hàng cho ProductVariant ID: " + productVariantId +
		    ". Có: " + available + ", yêu cầu: " + requested);
	}
}
