package com.ec.catalog.dto.productVariant;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantResponseDTO {
    private String id;
    private Integer volume;
    private BigDecimal price;
}