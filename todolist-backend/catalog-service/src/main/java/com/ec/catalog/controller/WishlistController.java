package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.wishlist.WishlistCreateForm;
import com.ec.catalog.dto.wishlist.WishlistDTO;
import com.ec.catalog.entity.Account;
import com.ec.catalog.entity.Product;
import com.ec.catalog.entity.Wishlist;
import com.ec.catalog.service.ProductImageService;
import com.ec.catalog.service.ProductVariantService;
import com.ec.catalog.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlists/me")
@CrossOrigin(origins = "*")
public class WishlistController {
	
	@Autowired
	private WishlistService wishlistService;
	
	@Autowired
	private ProductImageService productImageService;
	
	@Autowired
	private ProductVariantService productVariantService;
	
	/**
	 * GET: Lấy wishlist của user hiện tại
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<WishlistDTO>>> getMyWishlist() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		List<Wishlist> wishlists = wishlistService.getWishlistByAccountId(account.getId());
		
		List<WishlistDTO> dtos = wishlists.stream().map(wishlist -> {
			Product product = wishlist.getProduct();
			
			WishlistDTO dto = new WishlistDTO();
			dto.setId(product.getId());
			dto.setProductName(product.getProductName());
			dto.setSlug(product.getSlug());
			
			// Lấy thumbnail
			productImageService.getThumbnailByProductId(product.getId()).ifPresent(img -> {
				dto.setThumbnailUrl(img.getImageUrl());
			});
			
			// Lấy min/max price
			dto.setMinPrice(productVariantService.getMinPriceByProductId(product.getId()));
			dto.setMaxPrice(productVariantService.getMaxPriceByProductId(product.getId()));
			
			return dto;
		}).toList();
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", dtos));
	}
	
	/**
	 * POST: Thêm sản phẩm vào wishlist
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> addToWishlist(@RequestBody @Valid WishlistCreateForm form) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		wishlistService.addToWishlist(account, form.getProductId());
		return ResponseEntity.status(HttpStatus.CREATED)
		    .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Đã thêm vào wishlist", null));
	}
	
	/**
	 * DELETE: Xóa sản phẩm khỏi wishlist
	 */
	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@PathVariable String productId) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		wishlistService.removeFromWishlist(account.getId(), productId);
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Đã xóa khỏi wishlist", null));
	}
}
