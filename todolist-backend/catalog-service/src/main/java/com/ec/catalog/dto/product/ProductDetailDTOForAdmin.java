package com.ec.catalog.dto.product;

import com.ec.catalog.entity.Product;
import com.ec.catalog.entity.ProductImage;
import com.ec.catalog.entity.ProductVariant;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ProductDetailDTOForAdmin {

    private String id;
    private String productName;
    private String slug;
    private String description;
    private Integer vintage;
    private BigDecimal alcohol;
    private String region;
    private Boolean isPublished;
    private Boolean isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String categoryId;
    private String brandId;

    private List<ProductVariantDTO> variants;
    private List<ProductImageDTO> images;

    public static ProductDetailDTOForAdmin fromEntity(Product product) {
        return ProductDetailDTOForAdmin.builder()
            .id(product.getId())
            .productName(product.getProductName())
            .slug(product.getSlug())
            .description(product.getDescription())
            .vintage(product.getVintage())
            .alcohol(product.getAlcohol())
            .region(product.getRegion())
            .isPublished(product.getIsPublished())
            .isDeleted(product.getIsDeleted())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .categoryId(product.getCategory().getId())
            .brandId(product.getBrand().getId())
            .variants(
                product.getVariants().stream()
                    .filter(v -> !Boolean.TRUE.equals(v.getIsDeleted()))
                    .map(ProductVariantDTO::fromEntity)
                    .collect(Collectors.toList())
            )
            .images(
                product.getImages().stream()
                    .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                    .map(ProductImageDTO::fromEntity)
                    .collect(Collectors.toList())
            )
            .build();
    }

    // === Nested DTOs ===

    @Data
    @Builder
    public static class ProductVariantDTO {
        private String id;
        private Integer volume;
        private BigDecimal price;
        private Integer quantity;
        private Boolean isPublished;

        public static ProductVariantDTO fromEntity(ProductVariant variant) {
            return ProductVariantDTO.builder()
                .id(variant.getId())
                .volume(variant.getVolume())
                .price(variant.getPrice())
                .quantity(variant.getQuantity())
                .isPublished(variant.getIsPublished())
                .build();
        }
    }

    @Data
    @Builder
    public static class ProductImageDTO {
        private String id;
        private String imageUrl;
        private Boolean isThumbnail;

        public static ProductImageDTO fromEntity(ProductImage image) {
            return ProductImageDTO.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isThumbnail(image.getIsThumbnail())
                .build();
        }
    }
}
