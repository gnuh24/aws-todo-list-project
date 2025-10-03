package com.ec.order.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreateResponseDTO {
	
	private String id; // Mã đơn hàng
	
	private BigDecimal totalAmount; // Tổng tiền đơn hàng
	
	private String vnpUrl; // URL thanh toán VNPAY (nếu dùng VNPAY)
	
	// ✅ Có thể bổ sung thêm nếu bạn muốn hiện ở FE
	private String receiverName;
	private String receiverPhone;
	private String receiverAddress;
}
