package com.ec.catalog.service;

import com.ec.catalog.dto.category.CategoryCreateForm;
import com.ec.catalog.dto.category.CategoryUpdateForm;
import com.ec.catalog.entity.Category;
import com.ec.catalog.exceptions.business.category.CategoryAlreadyExistsException;
import com.ec.catalog.exceptions.business.category.CategoryNotFoundException;
import com.ec.catalog.repository.CategoryRepository;
import com.ec.catalog.specification.CategorySpecification;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

//    @Autowired
//    @Lazy
//    private ProductService productService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<Category> getAllCategoryNoPaging() {
		return categoryRepository.findAllByIsDeletedFalse();
	}
	
	@Override
	public Page<Category> getAllCategories(Pageable pageable, String search) {
		return categoryRepository.findAll(CategorySpecification.searchByName(search), pageable);
	}

	
	@Override
	public Category getCategoryById(String id) {
		return categoryRepository.findByIdAndIsDeletedFalse(id)
		    .orElseThrow(() -> new CategoryNotFoundException(id));
	}
	
	@Override
	public Category createCategory(CategoryCreateForm form) {
		if (categoryRepository.existsByCategoryNameIgnoreCaseAndIsDeletedFalse(form.getCategoryName())) {
			throw new CategoryAlreadyExistsException(form.getCategoryName());
		}
		
		Category category = Category.builder()
		    .categoryName(form.getCategoryName().trim())
		    .build();
		
		return categoryRepository.save(category);
	}
	
	@Override
	public Category updateCategory(String categoryId, CategoryUpdateForm form) {
		Category category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
		    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
		
		String newName = form.getCategoryName().trim();
		
		if (!category.getCategoryName().equalsIgnoreCase(newName) &&
		    categoryRepository.existsByCategoryNameIgnoreCaseAndIsDeletedFalse(newName)) {
			throw new CategoryAlreadyExistsException(newName);
		}
		
		category.setCategoryName(newName);
		return categoryRepository.save(category);
	}
	
	
	@Override
	public void deleteCategory(String categoryId) {
		Category category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
		    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
		
		category.setIsDeleted(true);
		category.setDeletedAt(LocalDateTime.now());
		
		categoryRepository.save(category);
	}




//    /**
//     * Validates if the category name already exists.
//     * If an ID is provided, it ensures the name is not used by another category.
//     *
//     * @param categoryName the category name to validate
//     * @param currentId the ID of the current category (for updates)
//     * @throws Exception if the category name is invalid
//     */
//    private void validateCategoryName(String categoryName, Integer currentId) throws Exception {
//	Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryName);
//
//	if (existingCategory.isPresent() && !existingCategory.get().getId().equals(currentId)) {
//	    throw new Exception("Tên danh mục '" + categoryName + "' đã tồn tại, vui lòng chọn tên khác!");
//	}
//    }
}
