package com.ec.catalog.repository;

import com.ec.catalog.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
	List<ProductImage> findByProductIdAndIsDeletedFalse(String productId);
	
	Optional<ProductImage> findByIdAndIsDeletedFalse(String productImageId);
}
