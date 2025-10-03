package com.ec.catalog.repository;

import com.ec.catalog.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, String>, JpaSpecificationExecutor<Brand> {
	List<Brand> findAllByIsDeletedFalse();
	boolean existsByBrandNameIgnoreCaseAndIsDeletedFalse(String name);
	Optional<Brand> findByIdAndIsDeletedFalse(String id);
	
}
