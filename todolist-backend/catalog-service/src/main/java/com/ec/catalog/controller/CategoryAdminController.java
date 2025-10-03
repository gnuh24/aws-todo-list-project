package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.category.CategoryCreateForm;
import com.ec.catalog.dto.category.CategoryResponseDTOForAdmin;
import com.ec.catalog.dto.category.CategoryUpdateForm;
import com.ec.catalog.entity.Category;
import com.ec.catalog.service.CategoryService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<Page<CategoryResponseDTOForAdmin>>> getAllCategories(
	    @RequestParam(required = false) String search,
	    Pageable pageable
	) {
		Page<Category> entities = categoryService.getAllCategories(pageable, search);
		Page<CategoryResponseDTOForAdmin> result = entities.map(CategoryResponseDTOForAdmin::fromEntity);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách danh mục thành công", result));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<CategoryResponseDTOForAdmin>> createCategory(
	    @Valid @RequestBody CategoryCreateForm form
	) {
		Category category = categoryService.createCategory(form);
		CategoryResponseDTOForAdmin result = CategoryResponseDTOForAdmin.fromEntity(category);
		return ResponseEntity.ok(new ApiResponse<>(200, "Tạo danh mục thành công", result));
	}
	
	@PatchMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<CategoryResponseDTOForAdmin>> updateCategory(
	    @PathVariable String categoryId,
	    @Valid @RequestBody CategoryUpdateForm form
	) {
		Category updated = categoryService.updateCategory(categoryId, form);
		return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật danh mục thành công", CategoryResponseDTOForAdmin.fromEntity(updated)));
	}
	
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable String categoryId) {
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa danh mục thành công", null));
	}
	
	
	
}
