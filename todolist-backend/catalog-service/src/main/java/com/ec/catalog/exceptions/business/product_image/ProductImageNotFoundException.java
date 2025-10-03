package com.ec.catalog.exceptions.business.product_image;

public class ProductImageNotFoundException extends RuntimeException {

    public ProductImageNotFoundException(String id) {
        super("Không tìm thấy ảnh sản phẩm với ID: " + id);
    }
}
