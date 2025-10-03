package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.brand.BrandCreateForm;
import com.ec.catalog.dto.brand.BrandResponseDTOForAdmin;
import com.ec.catalog.dto.brand.BrandUpdateForm;
import com.ec.catalog.entity.Brand;
import com.ec.catalog.service.BrandService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/brands")
public class BrandAdminController {
	
	@Autowired
	private BrandService brandService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<Page<BrandResponseDTOForAdmin>>> getAllBrands(
	    @RequestParam(required = false) String search,
	    Pageable pageable
	) {
		Page<Brand> entities = brandService.getAllBrands(pageable, search);
		Page<BrandResponseDTOForAdmin> result = entities.map(BrandResponseDTOForAdmin::fromEntity);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thương hiệu thành công", result));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<BrandResponseDTOForAdmin>> createBrand(
	    @Valid @RequestBody BrandCreateForm form
	) {
		Brand created = brandService.createBrand(form);
		return ResponseEntity.ok(new ApiResponse<>(200, "Tạo thương hiệu thành công", BrandResponseDTOForAdmin.fromEntity(created)));
	}
	
	@PatchMapping("/{brandId}")
	public ResponseEntity<ApiResponse<BrandResponseDTOForAdmin>> updateBrand(
	    @PathVariable String brandId,
	    @Valid @RequestBody BrandUpdateForm form
	) {
		Brand updated = brandService.updateBrand(brandId, form);
		return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thương hiệu thành công", BrandResponseDTOForAdmin.fromEntity(updated)));
	}
	
	@DeleteMapping("/{brandId}")
	public ResponseEntity<ApiResponse<Object>> deleteBrand(@PathVariable String brandId) {
		brandService.deleteBrand(brandId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa thương hiệu thành công", null));
	}
}
