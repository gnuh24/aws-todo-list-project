package com.ec.order.service;


import com.ec.order.dto.cartItem.CartItem;
import com.ec.order.dto.cartItem.CartItemCreateForm;
import com.ec.order.dto.cartItem.CartItemUpdateForm;
import com.ec.order.integration.redis.RedisConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
	
	private final RedisTemplate<String, Object> redisTemplate;
	
	private String buildCartItemKey(String accountId, String productId) {
		return RedisConstants.CART_ITEM + ":" + accountId + ":" + productId;
	}
	
	private String buildCartPatternKey(String accountId) {
		return RedisConstants.CART_ITEM + ":" + accountId + ":*";
	}
	
	@Override
	public List<CartItem> getMyCart(String accountId) {
		String pattern = buildCartPatternKey(accountId);
		Set<String> keys = redisTemplate.keys(pattern);
		List<CartItem> cartItems = new ArrayList<>();
		
		if (keys != null && !keys.isEmpty()) {
			List<Object> values = redisTemplate.opsForValue().multiGet(keys);
			if (values != null) {
				for (Object value : values) {
					if (value instanceof CartItem) {
						cartItems.add((CartItem) value);
					}
				}
			}
		}
		return cartItems;
	}
	
	
	@Override
	public CartItem addProductIntoCart(String accountId, CartItemCreateForm form) {
		String key = buildCartItemKey(accountId, form.getProductVariantId());
		CartItem cartItem = new CartItem();
		cartItem.setProductVariantId(form.getProductVariantId());
		cartItem.setQuantity(form.getQuantity());
		cartItem.setCreatedTime(System.currentTimeMillis());
		
		redisTemplate.opsForValue().set(key, cartItem);
		return cartItem;
	}
	
	@Override
	public CartItem updateCartItem(String accountId, CartItemUpdateForm form) {
		String key = buildCartItemKey(accountId, form.getProductVariantId());
		Object existing = redisTemplate.opsForValue().get(key);
		if (existing instanceof CartItem) {
			CartItem cartItem = (CartItem) existing;
			cartItem.setQuantity(form.getQuantity());
			redisTemplate.opsForValue().set(key, cartItem);
			return cartItem;
		}
		throw new RuntimeException("Cart item not found for update");
	}
	
	@Override
	public void removeProductFromCart(String accountId, String productVariantId) {
		String key = buildCartItemKey(accountId, productVariantId);
		redisTemplate.delete(key);
	}
	
	@Override
	public void clearCart(String accountId) {
		String pattern = buildCartPatternKey(accountId);
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}
}
