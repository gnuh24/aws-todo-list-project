package com.ec.catalog.dto.category;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryResponseDTO {
	
	private String id;
	
	private String categoryName;
	
	private Integer productCount;
	
}

