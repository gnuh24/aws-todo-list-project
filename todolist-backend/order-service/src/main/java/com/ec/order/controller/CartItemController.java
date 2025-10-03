package com.ec.order.controller;

import com.ec.order.entity.Account;
import com.ec.order.api.ApiResponse;
import com.ec.order.dto.cartItem.CartItem;
import com.ec.order.dto.cartItem.CartItemCreateForm;
import com.ec.order.dto.cartItem.CartItemUpdateForm;
import com.ec.order.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-item")
@CrossOrigin(origins = "*")
@Tag(name = "Cart Item", description = "Quản lý giỏ hàng của người dùng")
public class CartItemController {
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	// ORD-CART-001: GET - /api/order/cart-item/me
	@Operation(summary = "Lấy giỏ hàng của tôi", description = "Trả về tất cả sản phẩm trong giỏ hàng của người dùng hiện tại")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<List<CartItem>>> getMyCart() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		List<CartItem> cartItems = cartItemService.getMyCart(account.getId());
		List<CartItem> dtos = cartItems.stream()
		    .map(item -> modelMapper.map(item, CartItem.class))
		    .toList();
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy giỏ hàng thành công", dtos));
	}
	
	// ORD-CART-002: POST - /api/order/cart-item/me
	@Operation(summary = "Thêm sản phẩm vào giỏ", description = "Thêm sản phẩm mới vào giỏ hàng")
	@PostMapping("/me")
	public ResponseEntity<ApiResponse<CartItem>> addProductToCart(
	    @RequestBody @Valid CartItemCreateForm form) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		CartItem created = cartItemService.addProductIntoCart(account.getId(), form);
		CartItem dto = modelMapper.map(created, CartItem.class);
		
		return ResponseEntity.ok(new ApiResponse<>(201, "Thêm vào giỏ hàng thành công", dto));
	}
	
	// ORD-CART-003: PATCH - /api/order/cart-item/me
	@Operation(summary = "Cập nhật số lượng sản phẩm trong giỏ", description = "Cập nhật số lượng của sản phẩm trong giỏ hàng")
	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<CartItem>> updateCartItem(
	    @RequestBody @Valid CartItemUpdateForm form) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		CartItem updated = cartItemService.updateCartItem(account.getId(), form);
		CartItem dto = modelMapper.map(updated, CartItem.class);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật giỏ hàng thành công", dto));
	}
	
	// ORD-CART-004: DELETE - /api/order/cart-item/me/{productVariantId}
	@Operation(summary = "Xóa sản phẩm khỏi giỏ", description = "Xóa 1 sản phẩm ra khỏi giỏ hàng")
	@DeleteMapping("/me/{productVariantId}")
	public ResponseEntity<ApiResponse<Void>> removeProductFromCart(@PathVariable String productVariantId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		cartItemService.removeProductFromCart(account.getId(), productVariantId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa sản phẩm khỏi giỏ hàng thành công", null));
	}
	
	// ORD-CART-005: DELETE - /api/order/cart-item/me/clear
	@Operation(summary = "Xóa toàn bộ giỏ hàng", description = "Clear toàn bộ sản phẩm trong giỏ hàng")
	@DeleteMapping("/me/clear")
	public ResponseEntity<ApiResponse<Void>> clearCart() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		cartItemService.clearCart(account.getId());
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa toàn bộ giỏ hàng thành công", null));
	}
}
