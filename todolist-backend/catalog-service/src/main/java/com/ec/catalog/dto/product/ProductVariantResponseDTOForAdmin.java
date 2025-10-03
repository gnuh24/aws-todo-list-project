package com.ec.catalog.dto.product;

import com.ec.catalog.entity.ProductVariant;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductVariantResponseDTOForAdmin {

    private String id;
    private Integer volume;
    private BigDecimal price;
    private Integer quantity;
    private Boolean isPublished;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ProductVariantResponseDTOForAdmin fromEntity(ProductVariant variant) {
        return ProductVariantResponseDTOForAdmin.builder()
                .id(variant.getId())
                .volume(variant.getVolume())
                .price(variant.getPrice())
                .quantity(variant.getQuantity())
                .isPublished(variant.getIsPublished())
                .isDeleted(variant.getIsDeleted())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .deletedAt(variant.getDeletedAt())
                .build();
    }
}
