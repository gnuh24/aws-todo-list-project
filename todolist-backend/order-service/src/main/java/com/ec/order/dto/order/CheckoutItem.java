package com.ec.order.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutItem {
	private String productVariantId;
	private Integer quantity;
	
	// Các thông tin lấy từ Catalog Service (được set ở server)
	private String productName;
	private Integer productVolume;
	private String productThumbnail;
	private BigDecimal unitPrice;
}
