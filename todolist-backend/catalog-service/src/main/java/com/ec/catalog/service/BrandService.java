package com.ec.catalog.service;

import com.ec.catalog.entity.Brand;

import java.util.List;

import com.ec.catalog.dto.brand.BrandCreateForm;
import com.ec.catalog.dto.brand.BrandUpdateForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
	
	List<Brand> getAllBrandNoPaging();
	
	Page<Brand> getAllBrands(Pageable pageable, String search);
	
	Brand getBrandById(String id);
	
	Brand createBrand(BrandCreateForm form);
	
	Brand updateBrand(String id, BrandUpdateForm form);
	
	void deleteBrand(String id);
}

