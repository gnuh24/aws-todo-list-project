package com.ec.catalog.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateForm {
	@NotBlank(message = "Tên danh mục không được để trống")
	@Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
	private String categoryName;
}
