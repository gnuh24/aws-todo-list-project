package com.ec.catalog.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductImageForm {

    @NotBlank(message = "URL hình ảnh không được để trống")
    private String imageUrl;

    private Boolean isThumbnail;
}
