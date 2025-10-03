package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.productImage.ProductImageCreateForm;
import com.ec.catalog.dto.productImage.ProductImageResponseDTO;
import com.ec.catalog.entity.ProductImage;
import com.ec.catalog.service.ProductImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product-images")
public class ProductImageAdminController {
	
	@Autowired
	private ProductImageService productImageService;
	
	@PostMapping
	public ResponseEntity<ApiResponse<ProductImageResponseDTO>> createImage(
	    @RequestParam String productId,
	    @Valid @RequestBody ProductImageCreateForm form
	) {
		ProductImage image = productImageService.createImage(productId, form);
		return ResponseEntity.ok(new ApiResponse<>(200, "Tạo ảnh sản phẩm thành công", ProductImageResponseDTO.fromEntity(image)));
	}
	
	@PatchMapping("/{imageId}/set-thumbnail")
	public ResponseEntity<ApiResponse<ProductImageResponseDTO>> setThumbnail(@PathVariable String imageId) {
		ProductImage image = productImageService.setThumbnail(imageId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Đặt ảnh làm thumbnail thành công", ProductImageResponseDTO.fromEntity(image)));
	}
	
	@DeleteMapping("/{imageId}")
	public ResponseEntity<ApiResponse<Object>> deleteImage(@PathVariable String imageId) {
		productImageService.deleteImage(imageId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa ảnh thành công", null));
	}
}
