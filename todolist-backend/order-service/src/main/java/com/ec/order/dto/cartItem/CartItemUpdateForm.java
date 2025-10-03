package com.ec.order.dto.cartItem;

import lombok.Data;

@Data
public class CartItemUpdateForm {
    private String productVariantId;
    private Integer quantity;
}
