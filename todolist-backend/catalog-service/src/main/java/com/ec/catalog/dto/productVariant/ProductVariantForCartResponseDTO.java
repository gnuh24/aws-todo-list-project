package com.ec.catalog.dto.productVariant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantForCartResponseDTO {

    private String id;
    private String productName;
    private Integer volume;
    private String thumbnail;
}
