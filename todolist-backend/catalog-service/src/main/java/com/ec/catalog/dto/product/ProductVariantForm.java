package com.ec.catalog.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantForm {

    @Min(value = 10, message = "Dung tích phải lớn hơn 10ml")
    private Integer volume;

    @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Min(value = 0, message = "Số lượng không hợp lệ")
    private Integer quantity;

    private Boolean isPublished;
}
