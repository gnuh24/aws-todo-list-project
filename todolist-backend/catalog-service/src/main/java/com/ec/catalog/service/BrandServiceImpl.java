package com.ec.catalog.service;

import com.ec.catalog.dto.brand.BrandCreateForm;
import com.ec.catalog.dto.brand.BrandUpdateForm;
import com.ec.catalog.entity.Brand;
import com.ec.catalog.exceptions.business.brand.BrandAlreadyExistsException;
import com.ec.catalog.exceptions.business.brand.BrandNotFoundException;
import com.ec.catalog.repository.BrandRepository;
import com.ec.catalog.specification.BrandSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
	
	@Autowired
	private BrandRepository brandRepository;
	
	@Override
	public List<Brand> getAllBrandNoPaging() {
		return brandRepository.findAll()
		    .stream()
		    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
		    .toList();
	}
	
	@Override
	public Page<Brand> getAllBrands(Pageable pageable, String search) {
		return brandRepository.findAll(BrandSpecification.searchByName(search), pageable);
	}
	
	@Override
	public Brand getBrandById(String id) {
		return brandRepository.findByIdAndIsDeletedFalse(id)
		    .orElseThrow(() -> new BrandNotFoundException(id));
	}
	
	@Override
	public Brand createBrand(BrandCreateForm form) {
		if (brandRepository.existsByBrandNameIgnoreCaseAndIsDeletedFalse(form.getBrandName())) {
			throw new BrandAlreadyExistsException(form.getBrandName());
		}
		
		Brand brand = Brand.builder()
		    .brandName(form.getBrandName().trim())
		    .build();
		
		return brandRepository.save(brand);
	}
	
	@Override
	public Brand updateBrand(String id, BrandUpdateForm form) {
		Brand brand = brandRepository.findByIdAndIsDeletedFalse(id)
		    .orElseThrow(() -> new BrandNotFoundException(id));
		
		String newName = form.getBrandName().trim();
		if (!brand.getBrandName().equalsIgnoreCase(newName)
		    && brandRepository.existsByBrandNameIgnoreCaseAndIsDeletedFalse(newName)) {
			throw new BrandAlreadyExistsException(newName);
		}
		
		brand.setBrandName(newName);
		return brandRepository.save(brand);
	}
	
	@Override
	public void deleteBrand(String id) {
		Brand brand = brandRepository.findByIdAndIsDeletedFalse(id)
		    .orElseThrow(() -> new BrandNotFoundException(id));
		
		brand.setIsDeleted(true);
		brand.setDeletedAt(LocalDateTime.now());
		
		brandRepository.save(brand);
	}
}
