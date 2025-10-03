package com.ec.catalog.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantUpdateForm {
	
    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer quantity;

    private Boolean isPublished;
}
