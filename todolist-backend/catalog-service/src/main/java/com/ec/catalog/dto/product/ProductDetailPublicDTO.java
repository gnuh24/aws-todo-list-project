package com.ec.catalog.dto.product;

import com.ec.catalog.dto.brand.BrandInProductDTO;
import com.ec.catalog.dto.category.CategoryInProductDTO;
import com.ec.catalog.dto.productImage.ProductImageResponseDTO;
import com.ec.catalog.dto.productVariant.ProductVariantResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailPublicDTO {
	
	private String id;
	
	private String productName;
	
	private String slug;
	
	private String description;
	
	private Integer vintage;
	
	private BigDecimal alcohol;
	
	private String region;
	
	private BrandInProductDTO brand;
	
	private CategoryInProductDTO category;
	
	private List<ProductImageResponseDTO> images;
	
	private List<ProductVariantResponseDTO> variants;
	
	private Boolean isInWishlist;
	
}
