package com.ec.catalog.dto.wishlist;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WishlistCreateForm {
    @NotBlank
    private String productId;
}
