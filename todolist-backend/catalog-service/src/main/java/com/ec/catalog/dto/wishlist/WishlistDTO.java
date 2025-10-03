package com.ec.catalog.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {
	
	private String id;                 // Product ID
	
	private String productName;        // Tên sản phẩm
	
	private String slug;               // Slug để route
	
	private String thumbnailUrl;       // Ảnh thumbnail nếu có
	
	private BigDecimal minPrice;       // Giá thấp nhất trong các variant
	
	private BigDecimal maxPrice;       // Giá cao nhất trong các variant
}
