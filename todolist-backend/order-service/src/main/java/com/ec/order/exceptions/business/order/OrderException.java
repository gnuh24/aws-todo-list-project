package com.ec.order.exceptions.business.order;


import com.ec.order.exceptions.business.BusinessException;

/**
 * Base class cho các lỗi thuộc nghiệp vụ Order (tạo đơn, huỷ đơn, thanh toán...).
 * Mọi exception liên quan đến Order nên kế thừa từ lớp này.
 */
public abstract class OrderException extends BusinessException {
	
	public OrderException(String message) {
		super(message);
	}
	
}
