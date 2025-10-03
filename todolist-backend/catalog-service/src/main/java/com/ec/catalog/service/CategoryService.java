package com.ec.catalog.service;

import com.ec.catalog.dto.category.CategoryCreateForm;
import com.ec.catalog.dto.category.CategoryUpdateForm;
import com.ec.catalog.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
	
	List<Category> getAllCategoryNoPaging();
	
	Page<Category> getAllCategories(Pageable pageable, String search);
	
	Category getCategoryById(String id);
	Category createCategory(CategoryCreateForm form);
	
	Category updateCategory(String id, CategoryUpdateForm form)  ;
	void deleteCategory(String categoryId)  ;
	
}
