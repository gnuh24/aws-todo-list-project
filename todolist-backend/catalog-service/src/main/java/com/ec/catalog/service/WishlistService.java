package com.ec.catalog.service;

import com.ec.catalog.entity.Account;
import com.ec.catalog.entity.Wishlist;

import java.util.List;

public interface WishlistService {
    List<Wishlist> getWishlistByAccountId(String accountId);
    
    void addToWishlist(Account account, String productId) throws Exception;

    void removeFromWishlist(String accountId, String productId) throws Exception;

    boolean isProductInWishlist(String accountId, String productId);
}
