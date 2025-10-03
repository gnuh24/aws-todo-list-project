// ProductImageService.java
package com.ec.catalog.service;

import com.ec.catalog.dto.productImage.ProductImageCreateForm;
import com.ec.catalog.entity.ProductImage;
import java.util.List;
import java.util.Optional;

public interface ProductImageService {
	// Lấy tất cả ảnh (không bị xóa) theo productId
	List<ProductImage> getAllByProductId(String productId);
	
	// Lấy thumbnail của sản phẩm theo productId
	Optional<ProductImage> getThumbnailByProductId(String productId);
	
	ProductImage createImage(String productId, ProductImageCreateForm form);
	
	ProductImage setThumbnail(String imageId);
	
	void deleteImage(String imageId);

}


