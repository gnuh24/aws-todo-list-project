package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.productVariant.ProductVariantForCartResponseDTO;
import com.ec.catalog.dto.productVariant.ProductVariantForOrderDTO;
import com.ec.catalog.dto.productVariant.QuantityReduceRequest;
import com.ec.catalog.entity.ProductImage;
import com.ec.catalog.entity.ProductVariant;
import com.ec.catalog.service.ProductImageService;
import com.ec.catalog.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/product-variants")
@Tag(name = "Product Variant", description = "Quản lý Product Variant")
public class ProductVariantController {
	
	@Autowired
	private ProductVariantService productVariantService;
	
	@Autowired
	private ProductImageService productImageService;
	
	@GetMapping("/by-ids")
	public ResponseEntity<ApiResponse<List<ProductVariantForCartResponseDTO>>> getVariantsByIds(@RequestParam String ids) {
		List<String> idList = Arrays.asList(ids.split(","));
		List<ProductVariant> variants = productVariantService.getVariantsByIds(idList);
		
		List<ProductVariantForCartResponseDTO> dtos = variants.stream().map(variant -> {
			String productName = variant.getProduct().getProductName();
			
			// Gọi hàm getThumbnailByProductId, hàm này sẽ tự throw nếu không có thumbnail
			String thumbnail = productImageService
			    .getThumbnailByProductId(variant.getProduct().getId())
			    .get()  // Yên tâm get() vì nếu không có đã throw rồi
			    .getImageUrl();  // Giả sử ProductImage có getImageUrl()
			
			return new ProductVariantForCartResponseDTO(
			    variant.getId(),
			    productName,
			    variant.getVolume(),
			    thumbnail
			);
		}).toList();
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách variant thành công", dtos));
	}
	
	@GetMapping("/full-info/by-ids")
	public ResponseEntity<ApiResponse<List<ProductVariantForOrderDTO>>> getFullVariantsByIds(@RequestParam String ids) {
		List<String> idList = Arrays.asList(ids.split(","));
		List<ProductVariant> variants = productVariantService.getVariantsByIds(idList);
		
		List<ProductVariantForOrderDTO> dtos = variants.stream().map(variant -> {
			String productName = variant.getProduct().getProductName();
			
			String thumbnail = productImageService
			    .getThumbnailByProductId(variant.getProduct().getId())
			    .get()
			    .getImageUrl();
			
			return new ProductVariantForOrderDTO(
			    variant.getId(),
			    productName,
			    variant.getVolume(),
			    thumbnail,
			    variant.getPrice(),
			    variant.getQuantity(),
			    variant.getIsDeleted(),
			    variant.getIsPublished()
			);
		}).toList();
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin đầy đủ variant thành công", dtos));
	}
	
	@PostMapping("/reduce-quantity")
	public ResponseEntity<ApiResponse<Void>> reduceQuantities(@RequestBody List<QuantityReduceRequest> requests) {
		productVariantService.reduceQuantities(requests);
		return ResponseEntity.ok(new ApiResponse<>(200, "Giảm số lượng thành công", null));
	}
	
	
	
}
