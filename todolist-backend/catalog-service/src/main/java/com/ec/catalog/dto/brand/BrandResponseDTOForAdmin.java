package com.ec.catalog.dto.brand;

import com.ec.catalog.entity.Brand;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BrandResponseDTOForAdmin {
    private String id;
    private String brandName;
    private Integer productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BrandResponseDTOForAdmin fromEntity(Brand brand) {
        return BrandResponseDTOForAdmin.builder()
            .id(brand.getId())
            .brandName(brand.getBrandName())
            .productCount(brand.getProductCount())
            .createdAt(brand.getCreatedAt())
            .updatedAt(brand.getUpdatedAt())
            .build();
    }
}
