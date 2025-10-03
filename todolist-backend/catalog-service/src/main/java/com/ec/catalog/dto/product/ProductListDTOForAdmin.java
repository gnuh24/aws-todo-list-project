package com.ec.catalog.dto.product;

import com.ec.catalog.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductListDTOForAdmin {
    private String id;
    private String productName;
    private String brandName;
    private String categoryName;
    private Boolean isPublished;
    private LocalDateTime createdAt;

    public static ProductListDTOForAdmin fromEntity(Product product) {
        return ProductListDTOForAdmin.builder()
            .id(product.getId())
            .productName(product.getProductName())
            .brandName(product.getBrand().getBrandName())
            .categoryName(product.getCategory().getCategoryName())
            .isPublished(product.getIsPublished())
            .createdAt(product.getCreatedAt())
            .build();
    }
}
