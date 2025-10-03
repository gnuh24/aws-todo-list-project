package com.ec.catalog.service;

import com.ec.catalog.dto.product.ProductVariantForm;
import com.ec.catalog.dto.product.ProductVariantUpdateForm;
import com.ec.catalog.dto.productVariant.QuantityReduceRequest;
import com.ec.catalog.entity.Product;
import com.ec.catalog.entity.ProductVariant;
import com.ec.catalog.exceptions.business.product.ProductNotFoundException;
import com.ec.catalog.exceptions.business.product_variant.DuplicateVariantVolumeException;
import com.ec.catalog.exceptions.business.product_variant.ProductVariantNotFoundException;
import com.ec.catalog.exceptions.business.product_variant.ProductVariantQuantityNotEnough;
import com.ec.catalog.repository.ProductRepository;
import com.ec.catalog.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {
	
	@Autowired
	private ProductVariantRepository productVariantRepository;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<ProductVariant> getVariantsByIds(List<String> variantIds) {
		return productVariantRepository.findByIdIn(variantIds);
	}
	
	@Override
	public List<ProductVariant> getVariantsByProductId(String productId) {
		return productVariantRepository.findByProductIdAndIsDeletedFalse(productId);
	}
	
	@Override
	@Transactional
	public void reduceQuantities(List<QuantityReduceRequest> requests) {
		for (QuantityReduceRequest req : requests) {
			ProductVariant variant = productVariantRepository.findById(req.getProductVariantId())
			    .orElseThrow(() -> new ProductVariantNotFoundException(req.getProductVariantId()));
			
			int available = variant.getQuantity();
			int requested = req.getQuantity();
			if (available < requested) {
				throw new ProductVariantQuantityNotEnough(req.getProductVariantId(), requested, available);
			}
			
			variant.setQuantity(available - requested);
			// Có thể bỏ dòng dưới nếu bạn dùng JPA + dirty checking
			productVariantRepository.save(variant);
		}
	}


	
	@Override
	public BigDecimal getMinPriceByProductId(String productId) {
		return productVariantRepository.findMinPriceByProductId(productId);
	}
	
	@Override
	public BigDecimal getMaxPriceByProductId(String productId) {
		return productVariantRepository.findMaxPriceByProductId(productId);
	}
	
	@Override
	@Transactional
	public ProductVariant updateVariant(String variantId, ProductVariantUpdateForm form) {
		ProductVariant variant = productVariantRepository.findByIdAndIsDeletedFalse(variantId)
		    .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
		
		variant.setPrice(form.getPrice());
		variant.setQuantity(form.getQuantity());
		variant.setIsPublished(Boolean.TRUE.equals(form.getIsPublished()));
		
		return productVariantRepository.save(variant);
	}
	
	
	@Override
	@Transactional
	public void deleteVariant(String variantId) {
		ProductVariant variant = productVariantRepository.findByIdAndIsDeletedFalse(variantId)
		    .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
		
		variant.setIsDeleted(true);
		variant.setDeletedAt(LocalDateTime.now());
		
		productVariantRepository.save(variant);
	}
	
	@Override
	@Transactional
	public ProductVariant createVariant(String productId, ProductVariantForm form) {
		Product product = productService.getProductById(productId);
		Integer volume = form.getVolume();
		
		// Tìm các variant trùng volume (có thể là đã xóa hoặc chưa)
		Optional<ProductVariant> existing = productVariantRepository
		    .findByProductIdAndVolume(productId, volume);
		
		if (existing.isPresent()) {
			ProductVariant variant = existing.get();
			if (!Boolean.TRUE.equals(variant.getIsDeleted())) {
				// TH1: Variant chưa bị xóa -> báo lỗi
				throw new DuplicateVariantVolumeException(volume);
			}
			
			// TH2: Tái sử dụng variant đã bị xóa
			variant.setPrice(form.getPrice());
			variant.setQuantity(form.getQuantity());
			variant.setIsPublished(Boolean.TRUE.equals(form.getIsPublished()));
			variant.setIsDeleted(false);
			variant.setDeletedAt(null);
			return productVariantRepository.save(variant);
		}
		
		// TH3: Tạo mới
		ProductVariant newVariant = ProductVariant.builder()
		    .product(product)
		    .volume(volume)
		    .price(form.getPrice())
		    .quantity(form.getQuantity())
		    .isPublished(Boolean.TRUE.equals(form.getIsPublished()))
		    .isDeleted(false)
		    .build();
		
		return productVariantRepository.save(newVariant);
	}
	
	
	
}
