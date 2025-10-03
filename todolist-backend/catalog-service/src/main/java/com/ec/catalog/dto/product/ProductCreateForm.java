package com.ec.catalog.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCreateForm {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    @Size(max = 5000, message = "Mô tả quá dài")
    private String description;

    private Integer vintage; // Có thể null

    @DecimalMin(value = "0.0", inclusive = false, message = "Độ cồn phải lớn hơn 0")
    private BigDecimal alcohol;

    private String region;

    private Boolean isPublished;

    @NotBlank(message = "ID danh mục không được để trống")
    private String categoryId;

    @NotBlank(message = "ID thương hiệu không được để trống")
    private String brandId;

    @Valid
    @NotEmpty(message = "Cần ít nhất một phiên bản sản phẩm")
    private List<ProductVariantForm> variants;

    @Valid
    @NotEmpty(message = "Cần ít nhất một hình ảnh sản phẩm")
    private List<ProductImageForm> images;
}
