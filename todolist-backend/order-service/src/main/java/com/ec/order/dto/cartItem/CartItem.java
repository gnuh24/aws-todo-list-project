package com.ec.order.dto.cartItem;

import lombok.Data;

@Data
public class CartItem {
    private String productVariantId;
    private Integer quantity;
    private Long createdTime;
}
