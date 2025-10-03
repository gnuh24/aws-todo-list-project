package com.ec.catalog.repository;

import com.ec.catalog.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
	List<ProductVariant> findByProductIdAndIsDeletedFalse(String productId);
	List<ProductVariant> findByIdIn(List<String> ids);
	
	@Query("SELECT MIN(v.price) FROM ProductVariant v WHERE v.product.id = :productId AND v.isDeleted = false")
	BigDecimal findMinPriceByProductId(@Param("productId") String productId);
	
	@Query("SELECT MAX(v.price) FROM ProductVariant v WHERE v.product.id = :productId AND v.isDeleted = false")
	BigDecimal findMaxPriceByProductId(@Param("productId") String productId);
	
	Optional<ProductVariant> findByIdAndIsDeletedFalse(String productVariantId);
	
	Optional<ProductVariant> findByProductIdAndVolume(String productId, Integer volume);
	
	
}
