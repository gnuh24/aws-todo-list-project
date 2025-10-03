package com.ec.catalog.dto.productImage;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductImageCreateForm {
    @NotBlank
    private String imageUrl;

    private Boolean isThumbnail;
}
