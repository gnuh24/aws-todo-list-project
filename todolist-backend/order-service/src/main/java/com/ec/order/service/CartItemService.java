package com.ec.order.service;

import com.ec.order.dto.cartItem.CartItem;
import com.ec.order.dto.cartItem.CartItemCreateForm;
import com.ec.order.dto.cartItem.CartItemUpdateForm;

import java.util.List;

public interface CartItemService {
	List<CartItem> getMyCart(String accountId);
	
	CartItem addProductIntoCart(String accountId, CartItemCreateForm form);
	
	CartItem updateCartItem(String accountId, CartItemUpdateForm form);
	
	void removeProductFromCart(String accountId, String productVariantId);
	
	void clearCart(String accountId);
}
