package com.ec.catalog.repository;

import com.ec.catalog.entity.Brand;
import com.ec.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,  Integer>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByIdAndIsDeletedFalse(String categoryId);
	
	List<Category> findAllByIsDeletedFalse();
	boolean existsByCategoryNameIgnoreCaseAndIsDeletedFalse(String categoryName);
}

