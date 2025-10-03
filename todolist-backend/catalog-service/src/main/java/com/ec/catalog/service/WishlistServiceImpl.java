package com.ec.catalog.service;

import com.ec.catalog.entity.Account;
import com.ec.catalog.entity.Product;
import com.ec.catalog.entity.Wishlist;
import com.ec.catalog.entity.Wishlist.WishlistId;
import com.ec.catalog.repository.ProductRepository;
import com.ec.catalog.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {
	
	@Autowired
	private WishlistRepository wishlistRepository;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<Wishlist> getWishlistByAccountId(String accountId) {
		return wishlistRepository.findById_AccountId(accountId);
	}
	
	@Override
	public void addToWishlist(Account account, String productId) throws Exception {
		// Check product tồn tại và chưa bị xóa
		Product product = productService.getProductById(productId);
		
		WishlistId wishlistId = new WishlistId(account.getId(), productId);

//        Optional<Wishlist> existing = wishlistRepository.findById(wishlistId);
//        if (existing.isPresent()) {
//            throw new Exception("Sản phẩm đã có trong wishlist");
//        }
		
		Wishlist wishlist = new Wishlist();
		wishlist.setId(wishlistId);
		
		wishlist.setProduct(product);
		wishlist.setAccount(account);
		wishlistRepository.save(wishlist);
	}
	
	@Override
	public void removeFromWishlist(String accountId, String productId) throws Exception {
		WishlistId wishlistId = new WishlistId(accountId, productId);
		
		Wishlist wishlist = wishlistRepository.findById(wishlistId)
		    .orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại trong wishlist"));
		
		wishlistRepository.delete(wishlist);
	}
	
	@Override
	public boolean isProductInWishlist(String accountId, String productId) {
		WishlistId wishlistId = new WishlistId(accountId, productId);
		return wishlistRepository.existsById(wishlistId);
	}
}
