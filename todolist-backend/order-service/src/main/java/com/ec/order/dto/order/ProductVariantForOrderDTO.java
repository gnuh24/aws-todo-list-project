package com.ec.order.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantForOrderDTO {
	private String productVariantId;
	private String productName;
	private Integer volume;
	private String thumbnail;
	private BigDecimal unitPrice;
	private Integer quantity;
	private Boolean isDeleted;
	private Boolean isPublished;
}
