package com.ec.catalog.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListPublicDTO {
	
	private String id;
	
	private String productName;
	
	private String slug;
	
	private String thumbnailUrl; // ảnh thumbnail duy nhất (nếu có)
	
	private BigDecimal minPrice;
	
	private BigDecimal maxPrice;
	
//	private String brandName;
//
//	private String categoryName;

}
