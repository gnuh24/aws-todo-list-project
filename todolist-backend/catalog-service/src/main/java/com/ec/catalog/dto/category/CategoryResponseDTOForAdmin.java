package com.ec.catalog.dto.category;

import com.ec.catalog.entity.Category;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponseDTOForAdmin {
	
	private String id;
	private String categoryName;
	private Integer productCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// Hàm tiện lợi để chuyển từ Entity sang DTO
	public static CategoryResponseDTOForAdmin fromEntity(Category category) {
		return CategoryResponseDTOForAdmin.builder()
			.id(category.getId())
			.categoryName(category.getCategoryName())
			.productCount(category.getProductCount())
			.createdAt(category.getCreatedAt())
			.updatedAt(category.getUpdatedAt())
			.build();
	}
}
