package com.ec.catalog.dto.productImage;

import com.ec.catalog.entity.ProductImage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponseDTO {
    private String id;
    private String imageUrl;
    private Boolean isThumbnail;

    public static ProductImageResponseDTO fromEntity(ProductImage image) {
        return ProductImageResponseDTO.builder()
            .id(image.getId())
            .imageUrl(image.getImageUrl())
            .isThumbnail(image.getIsThumbnail())
            .build();
    }
}
