package com.ec.catalog.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateForm {
    
    @NotBlank
    private String productName;

    private String description;

    @Min(1900)
    @Max(2100)
    private Integer vintage;

    @DecimalMin("0.0")
    private BigDecimal alcohol;

    private String region;

    private Boolean isPublished;

    @NotBlank
    private String categoryId;

    @NotBlank
    private String brandId;
}
