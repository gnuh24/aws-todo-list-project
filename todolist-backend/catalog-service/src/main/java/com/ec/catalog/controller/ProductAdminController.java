package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.product.*;
import com.ec.catalog.dto.productVariant.ProductVariantResponseDTO;
import com.ec.catalog.entity.Product;
import com.ec.catalog.entity.ProductVariant;
import com.ec.catalog.repository.ProductVariantRepository;
import com.ec.catalog.service.ProductService;

import com.ec.catalog.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
public class ProductAdminController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductVariantService productVariantService;
	
	// === GET LIST PRODUCTS ===
	@GetMapping
	public ResponseEntity<ApiResponse<Page<ProductListDTOForAdmin>>> getAllProductsForAdmin(
	    @RequestParam(required = false) String search,
	    @RequestParam(required = false) Boolean isPublished,
	    @RequestParam(required = false) String categoryId,
	    @RequestParam(required = false) String brandId,
	    Pageable pageable
	) {
		Page<Product> entities = productService.filterProductsForAdmin(search, isPublished, categoryId, brandId, pageable);
		Page<ProductListDTOForAdmin> result = entities.map(ProductListDTOForAdmin::fromEntity);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách sản phẩm thành công", result));
	}
	
	// === GET DETAIL PRODUCT ===
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailDTOForAdmin>> getProductDetailForAdmin(
	    @PathVariable String productId
	) {
		Product product = productService.getProductById(productId);
		ProductDetailDTOForAdmin dto = ProductDetailDTOForAdmin.fromEntity(product);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết sản phẩm thành công", dto));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<ProductDetailDTOForAdmin>> createProduct(
	    @Valid @RequestBody ProductCreateForm form
	) {
		Product product = productService.createProduct(form);
		ProductDetailDTOForAdmin result = ProductDetailDTOForAdmin.fromEntity(product);
		return ResponseEntity.ok(new ApiResponse<>(200, "Tạo sản phẩm thành công", result));
	}
	
	@PatchMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailDTOForAdmin>> updateProduct(
	    @PathVariable String productId,
	    @Valid @RequestBody ProductUpdateForm form
	) {
		Product updated = productService.updateProduct(productId, form);
		ProductDetailDTOForAdmin result = ProductDetailDTOForAdmin.fromEntity(updated);
		return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật sản phẩm thành công", result));
	}
	
	
	@PatchMapping("/product-variants/{variantId}")
	public ResponseEntity<ApiResponse<ProductVariantResponseDTOForAdmin>> updateVariant(
	    @PathVariable String variantId,
	    @Valid @RequestBody ProductVariantUpdateForm form
	) {
		ProductVariant updated = productVariantService.updateVariant(variantId, form);
		return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật phiên bản sản phẩm thành công", ProductVariantResponseDTOForAdmin.fromEntity(updated)));
	}
	
	@PostMapping("/product-variants")
	public ResponseEntity<ApiResponse<ProductVariantResponseDTOForAdmin>> createVariant(
	    @RequestParam String productId,
	    @Valid @RequestBody ProductVariantForm form
	) {
		ProductVariant variant = productVariantService.createVariant(productId, form);
		ProductVariantResponseDTOForAdmin result = ProductVariantResponseDTOForAdmin.fromEntity(variant);
		return ResponseEntity.ok(new ApiResponse<>(200, "Tạo phiên bản sản phẩm thành công", result));
	}
	
	@DeleteMapping("/product-variants/{variantId}")
	public ResponseEntity<ApiResponse<Object>> deleteVariant(@PathVariable String variantId) {
		productVariantService.deleteVariant(variantId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa phiên bản sản phẩm thành công", null));
	}
	
}
