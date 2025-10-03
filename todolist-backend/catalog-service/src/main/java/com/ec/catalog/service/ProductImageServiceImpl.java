package com.ec.catalog.service;

import com.ec.catalog.dto.productImage.ProductImageCreateForm;
import com.ec.catalog.entity.Product;
import com.ec.catalog.entity.ProductImage;
import com.ec.catalog.exceptions.business.product.ProductNotFoundException;
import com.ec.catalog.exceptions.business.product_image.ProductImageNotFoundException;
import com.ec.catalog.repository.ProductImageRepository;
import com.ec.catalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductImageServiceImpl implements ProductImageService {
	
	@Autowired
	private ProductImageRepository productImageRepository;
	
	@Override
	public List<ProductImage> getAllByProductId(String productId) {
		return productImageRepository.findByProductIdAndIsDeletedFalse(productId);
	}
	
	@Override
	public Optional<ProductImage> getThumbnailByProductId(String productId) {
		Optional<ProductImage> optionalThumbnail = productImageRepository.findByProductIdAndIsDeletedFalse(productId)
		    .stream()
		    .filter(ProductImage::getIsThumbnail)
		    .findFirst();
		
		if (optionalThumbnail.isEmpty()) {
			throw new EntityNotFoundException("Không tìm thấy thumbnail cho productId: " + productId);
		}
		
		return optionalThumbnail;
	}
	
	@Autowired
	private ProductService productService;
	
	
	@Override
	@Transactional
	public ProductImage createImage(String productId, ProductImageCreateForm form) {
		Product product = productService.getProductById(productId);
		
		// Nếu là thumbnail thì clear các thumbnail trước
		if (Boolean.TRUE.equals(form.getIsThumbnail())) {
			List<ProductImage> allImages = productImageRepository.findByProductIdAndIsDeletedFalse(productId);
			for (ProductImage img : allImages) {
				img.setIsThumbnail(false);
			}
		}
		
		ProductImage image = ProductImage.builder()
		    .imageUrl(form.getImageUrl())
		    .isThumbnail(Boolean.TRUE.equals(form.getIsThumbnail()))
		    .isDeleted(false)
		    .product(product)
		    .build();
		
		return productImageRepository.save(image);
	}
	
	@Override
	@Transactional
	public ProductImage setThumbnail(String imageId) {
		ProductImage image = productImageRepository.findByIdAndIsDeletedFalse(imageId)
		    .orElseThrow(() -> new ProductImageNotFoundException(imageId));
		
		// Xóa trạng thái thumbnail hiện tại của các ảnh thuộc cùng sản phẩm
		List<ProductImage> allImages = productImageRepository.findByProductIdAndIsDeletedFalse(image.getProduct().getId());
		for (ProductImage img : allImages) {
			img.setIsThumbnail(false);
		}
		
		image.setIsThumbnail(true);
		return productImageRepository.save(image);
	}
	
	@Override
	@Transactional
	public void deleteImage(String imageId) {
		ProductImage image = productImageRepository.findByIdAndIsDeletedFalse(imageId)
		    .orElseThrow(() -> new ProductImageNotFoundException(imageId));
		
		image.setIsDeleted(true);
		image.setDeletedAt(LocalDateTime.now());
		productImageRepository.save(image);
	}

	
	
}
