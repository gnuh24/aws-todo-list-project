package com.ec.catalog.dto.productVariant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityReduceRequest {
    private String productVariantId;
    private int quantity;
}
