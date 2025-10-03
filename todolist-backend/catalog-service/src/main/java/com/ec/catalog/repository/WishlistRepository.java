package com.ec.catalog.repository;

import com.ec.catalog.entity.Wishlist;
import com.ec.catalog.entity.Wishlist.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
	
	List<Wishlist> findById_AccountId(String accountId);
	
	boolean existsByIdAccountIdAndIdProductId(String accountId, String productId);
	
	void deleteByIdAccountIdAndIdProductId(String accountId, String productId);
}
