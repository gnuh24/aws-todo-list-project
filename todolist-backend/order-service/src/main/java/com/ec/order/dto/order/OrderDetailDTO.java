package com.ec.order.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
	private String productVariantId;
	private String productName;
	private String productThumbnail;
	private Integer productVolume;
	private BigDecimal unitPrice;
	private Integer quantity;
	private BigDecimal totalPrice;
}
