package com.ec.order.dto.cartItem;

import lombok.Data;

@Data
public class CartItemCreateForm {
    private String productVariantId;
    private Integer quantity;
}
